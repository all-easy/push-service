package ru.all_easy.push.telegram.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import redis.clients.jedis.Jedis;
import ru.all_easy.push.common.IntegrationTest;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.repository.ExpenseRepository;
import ru.all_easy.push.room.repository.RoomRepository;
import ru.all_easy.push.telegram.api.controller.model.*;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.CommandsContextService;
import ru.all_easy.push.telegram.commands.rules.SplitCommandRule;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

    private static Jedis jedis;

    @BeforeEach
    void init() {
        testData.init();
    }

    @Test
    void postPositiveTest_ResultCommand_WithoutRedisCache() throws Exception {
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
                        .header("X-Telegram-Bot-Api-Secret-Token", "1234")
                        .content(updateAsString))
                .andDo(print());

        ArgumentCaptor<SendMessageInfo> argumentCaptor = ArgumentCaptor.forClass(SendMessageInfo.class);

        verify(telegramService, times(1)).sendMessage(argumentCaptor.capture());
        verify(telegramService).sendMessage(eq(expectedObject));
    }

    //    @Test
//    //    @Order(3)
//    void postPositiveTest_ResultCommand_WithRedisCache() throws Exception {
//
//        // TODO: Repair deserialization issue
//        Map<String, BigDecimal> cache = new HashMap<>();
//        cache.put("redis-test-response-value", new BigDecimal(10));
//        jedis.set("results::11111,USD", objectMapper.writeValueAsString(cache));
//
//        Message message = new Message(
//                1,
//                new User(1L, false, "user_1_nickname", "user_1"),
//                new Chat(11111L, "group", null, "room_title_1"),
//                1686759117L,
//                "/result",
//                null,
//                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
//
//        Update update = new Update(null, message);
//
//        String updateAsString = objectMapper.writeValueAsString(update);
//
//        when(telegramService.sendMessage(any(SendMessageInfo.class))).thenAnswer(inv -> {
//            Object[] args = inv.getArguments();
//            return objectMapper.writeValueAsString(args[0]);
//        });
//
//        String responseText = objectMapper.writeValueAsString(cache);
//        SendMessageInfo expectedObject = new SendMessageInfo(11111L, responseText, "Markdown");
//
//        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(updateAsString))
//                .andDo(print());
//
//        ArgumentCaptor<SendMessageInfo> argumentCaptor = ArgumentCaptor.forClass(SendMessageInfo.class);
//        verify(telegramService, times(1)).sendMessage(argumentCaptor.capture());
//        SendMessageInfo capturedArgument = argumentCaptor.getValue();
//        assertEquals(expectedObject, capturedArgument);
//        verify(telegramService).sendMessage(eq(expectedObject));
//
//        assertEquals(1, jedis.keys("*").size());
//        assertEquals("redis-test-response-value", jedis.get("results::11111,USD"));
//    }

//    @Test
//    @Order(4)
//    void postPositiveTest_SplitCommand() throws Exception {
//
//        Message message = new Message(
//                1,
//                new User(1L, false, "user_1_nickname", "user_1"),
//                new Chat(11111L, "group", null, "room_title_1"),
//                1686759117L,
//                "/split 100 test-description",
//                null,
//                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
//
//        Update update = new Update(null, message);
//
//        String updateAsString = objectMapper.writeValueAsString(update);
//
//        when(telegramService.sendMessage(any(SendMessageInfo.class))).thenAnswer(inv -> {
//            Object[] args = inv.getArguments();
//            return objectMapper.writeValueAsString(args[0]);
//        });
//
//        String responseText =
//                """
//                Expense *33.33* $ USD to user *user_2* has been successfully added, description: test-description
//                Expense *33.33* $ USD to user *user_3* has been successfully added, description: test-description
//                """;
//
//        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(updateAsString))
//                .andDo(print());
//
//        ArgumentCaptor<SendMessageInfo> argumentCaptor = ArgumentCaptor.forClass(SendMessageInfo.class);
//
//        verify(telegramService, times(1)).sendMessage(argumentCaptor.capture());
//
//        SendMessageInfo capturedArgument = argumentCaptor.getValue();
//
//        assertEquals(11111L, capturedArgument.chatId());
//        assertEquals("Markdown", capturedArgument.parseMode());
//        assertEquals(capturedArgument.text(), responseText);
//        assertNull(capturedArgument.replayId());
//        assertNull(capturedArgument.replayMarkup());
//
//        assertEquals(0, jedis.keys("*").size());
//        assertNull(jedis.get("results::11111,USD"));
//    }
}
