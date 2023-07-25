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
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

/**
 * Controller integration testing
 *
 * @see UpdateController
 * @see CommandsContextService
 * @see SplitCommandRule
 */
class UpdateControllerSplitCommandTest extends IntegrationTest {

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
    void postPositiveTest() throws Exception {

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

    @Test
    void postNegativeTest_INCORRECT_FORMAT() throws Exception {

        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        String updateAsString = objectMapper.writeValueAsString(update);

        when(telegramService.sendMessage(any(SendMessageInfo.class))).thenAnswer(inv -> {
            Object[] args = inv.getArguments();
            return objectMapper.writeValueAsString(args[0]);
        });

        String responseText =
                AnswerMessageTemplate.INCORRECT_FORMAT.getMessage().replace("/push", "/split");
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

    @Test
    void postNegativeTest_UNREGISTERED_ROOM() throws Exception {

        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(33333L, "group", null, "room_title_1"),
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

        String responseText = AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage();
        SendMessageInfo expectedObject = new SendMessageInfo(33333L, responseText, "Markdown");

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

    @Test
    void postNegativeTest_UNSET_CURRENCY() throws Exception {

        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(22222L, "group", null, "room_title_1"),
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

        String responseText = AnswerMessageTemplate.UNSET_CURRENCY.getMessage();
        SendMessageInfo expectedObject = new SendMessageInfo(22222L, responseText, "Markdown");

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
