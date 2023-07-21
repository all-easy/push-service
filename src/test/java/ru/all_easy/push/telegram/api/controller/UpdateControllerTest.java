package ru.all_easy.push.telegram.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.all_easy.push.common.IntegrationTest;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.repository.ExpenseRepository;
import ru.all_easy.push.room.repository.RoomRepository;
import ru.all_easy.push.telegram.api.controller.model.*;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.CommandsContextService;
import ru.all_easy.push.telegram.commands.rules.SplitCommandRule;

/**
 * Controller integration testing
 *
 * @see UpdateController
 * @see CommandsContextService
 * @see SplitCommandRule
 */
class UpdateControllerTest extends IntegrationTest {

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

    private static final String URL = "/v1/api/telegram/";
    private static final String HEADER_SECRET = "1234";

    @BeforeEach
    void beforeEach() {
        jedis.flushAll();
        Set<String> keys = jedis.keys("*");
        assertTrue(keys.isEmpty());

        testData.init();
    }

    @Test
    void postPositiveTest_ResultCommand_WithoutPriorRedisCache() throws Exception {

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

    //        @Test
    //        void postPositiveTest_ResultCommand_WithPriorRedisCache() throws Exception {
    //
    //            // TODO: Repair deserialization issue
    //            Map<String, BigDecimal> cache = new HashMap<>();
    //            cache.put("redis-test-response-value", new BigDecimal(10));
    //            jedis.set("results::11111,USD", objectMapper.writeValueAsString(cache));
    //
    //            Message message = new Message(
    //                    1,
    //                    new User(1L, false, "user_1_nickname", "user_1"),
    //                    new Chat(11111L, "group", null, "room_title_1"),
    //                    1686759117L,
    //                    "/result",
    //                    null,
    //                    Collections.singletonList(new MessageEntity("bot_command", 0, null)));
    //
    //            Update update = new Update(null, message);
    //
    //            String updateAsString = objectMapper.writeValueAsString(update);
    //
    //            when(telegramService.sendMessage(any(SendMessageInfo.class))).thenAnswer(inv -> {
    //                Object[] args = inv.getArguments();
    //                return objectMapper.writeValueAsString(args[0]);
    //            });
    //
    //            String responseText = objectMapper.writeValueAsString(cache);
    //            SendMessageInfo expectedObject = new SendMessageInfo(11111L, responseText, "Markdown");
    //
    //            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
    //                            .header("X-Telegram-Bot-Api-Secret-Token", HEADER_SECRET)
    //                            .content(updateAsString))
    //                    .andDo(print());
    //
    //            // TODO: expected to use cache instead of calling regular method to calculate result
    //
    //            ArgumentCaptor<SendMessageInfo> argumentCaptor = ArgumentCaptor.forClass(SendMessageInfo.class);
    //            verify(telegramService, times(1)).sendMessage(argumentCaptor.capture());
    //            SendMessageInfo capturedArgument = argumentCaptor.getValue();
    //            assertEquals(expectedObject, capturedArgument);
    //            verify(telegramService).sendMessage(eq(expectedObject));
    //
    //            assertEquals(1, jedis.keys("*").size());
    //            assertEquals("redis-test-response-value", jedis.get("results::11111,USD"));
    //        }

    @Test
    void postPositiveTest_SplitCommand() throws Exception {

        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 test-description",
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
                Expense *33.33* $ USD to user *user_2* has been successfully added, description: test-description
                Expense *33.33* $ USD to user *user_3* has been successfully added, description: test-description
                """;

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

        // Cache records should be evicted after split command is called
        assertEquals(0, jedis.keys("*").size());
        assertNull(jedis.get("results::11111,USD"));
    }
}
