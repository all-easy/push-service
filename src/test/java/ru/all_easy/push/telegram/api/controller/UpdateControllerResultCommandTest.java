package ru.all_easy.push.telegram.api.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import ru.all_easy.push.common.IntegrationTest;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.repository.ExpenseRepository;
import ru.all_easy.push.room.repository.RoomRepository;
import ru.all_easy.push.telegram.api.controller.model.*;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.CommandsContextService;
import ru.all_easy.push.telegram.commands.rules.ResultGroupCommandRule;

/**
 * Controller integration testing
 *
 * @see UpdateController
 * @see CommandsContextService
 * @see ResultGroupCommandRule
 */
class UpdateControllerResultCommandTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TelegramService telegramService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private TestData testData;

    public static Jedis jedis;

    private static final String URL = "/v1/api/telegram/";
    private static final String HEADER_SECRET = "1234";

    @Container
    private static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis"))
            .withExposedPorts(6379)
            .waitingFor(Wait.defaultWaitStrategy());

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);

        jedis = new Jedis(redisContainer.getHost(), redisContainer.getFirstMappedPort());
    }

    @BeforeAll
    static void beforeAll() {
        assertThat(redisContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void beforeEach() {
        jedis.flushAll();
        Set<String> keys = jedis.keys("*");
        assertTrue(keys.isEmpty());

        testData.init();
    }

    @Test
    void postPositiveTest() throws Exception {

        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/result",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        String updateAsString = objectMapper.writeValueAsString(update);

        when(telegramService.sendMessage(any(SendMessageInfo.class))).thenAnswer(inv -> {
            Object[] args = inv.getArguments();
            return objectMapper.writeValueAsString(args[0]);
        });

        String responseText =
                """
                *user_2* owes *user_1* sum: *80.00*
                *user_3* owes *user_1* sum: *20.00*
                *user_3* owes *user_2* sum: *40.00* $ USD""";

        SendMessageInfo expectedObject = new SendMessageInfo(11111L, responseText, "Markdown");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Telegram-Bot-Api-Secret-Token", HEADER_SECRET)
                        .content(updateAsString))
                .andDo(print());

        ArgumentCaptor<SendMessageInfo> argumentCaptor = ArgumentCaptor.forClass(SendMessageInfo.class);
        verify(telegramService, times(1)).sendMessage(argumentCaptor.capture());
        SendMessageInfo capturedArgument = argumentCaptor.getValue();
        assertEquals(expectedObject.chatId(), capturedArgument.chatId());
        assertEquals(expectedObject.parseMode(), capturedArgument.parseMode());
        assertEquals(expectedObject.text(), capturedArgument.text());
        assertNull(capturedArgument.replayId());
        assertNull(capturedArgument.replayMarkup());

        verify(telegramService).sendMessage(eq(expectedObject));

        // Cache record should be created after result command is called
        assertEquals(1, jedis.keys("*").size());
    }
}
