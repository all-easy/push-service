package ru.all_easy.push.telegram.commands.rules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.api.controller.model.*;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.service.SplitCommandService;
import ru.all_easy.push.telegram.commands.validators.SplitCommandValidator;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;

@ExtendWith(MockitoExtension.class)
class SplitCommandRuleTest {
    @InjectMocks
    SplitCommandRule splitCommandRule;

    @Mock
    SplitCommandValidator splitCommandValidator;

    @Mock
    SplitCommandService splitCommandService;

    @Test
    void applySplitCommand_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        boolean actual = splitCommandRule.apply(update);
        assertTrue(actual);
    }

    @Test
    void applySplitCommand_Amount_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 10",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        boolean actual = splitCommandRule.apply(update);
        assertTrue(actual);
    }

    @Test
    void applySplitCommand_AmountAndDescription_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 10 desc1 desc2",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        boolean actual = splitCommandRule.apply(update);
        assertTrue(actual);
    }

    @Test
    void applySplitCommand_AmountAndDescriptionAndPercent_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 10 desc1 desc2 10%",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        boolean actual = splitCommandRule.apply(update);
        assertTrue(actual);
    }

    @Test
    void applySplitCommand_NoArgs_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        boolean actual = splitCommandRule.apply(update);
        assertTrue(actual);
    }

    @Test
    void applySplitCommand_Negative() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/result",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        boolean actual = splitCommandRule.apply(update);
        assertFalse(actual);
    }

    @Test
    void process_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        SplitCommandValidated validatedMock = new SplitCommandValidated();
        validatedMock.setChatId(11111L);
        validatedMock.setFromUsername("user_1");
        validatedMock.setAmount(BigDecimal.valueOf(100.0));
        validatedMock.setDescription("test-description");
        ResultK<SplitCommandValidated, ValidationError> validatedResultMock = ResultK.Ok(validatedMock);
        when(splitCommandValidator.validate(update)).thenReturn(validatedResultMock);

        ResultK<CommandProcessed, CommandError> processedResultMock =
                ResultK.Ok(new CommandProcessed(11111L, "response-text"));
        when(splitCommandService.split(validatedMock)).thenReturn(processedResultMock);

        ResultK<CommandProcessed, CommandError> actual = splitCommandRule.process(update);
        assertNull(actual.getError());
        assertEquals(
                processedResultMock.getResult().chatId(), actual.getResult().chatId());
        assertEquals(
                processedResultMock.getResult().message(), actual.getResult().message());
        verify(splitCommandValidator, times(1)).validate(eq(update));
        verify(splitCommandService, times(1)).split(eq(validatedMock));
    }

    @Test
    void process_ValidationException_Negative() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        ResultK<SplitCommandValidated, ValidationError> validatedResultMock =
                ResultK.Err(new ValidationError("validation-error-text"));
        when(splitCommandValidator.validate(update)).thenReturn(validatedResultMock);

        ResultK<CommandProcessed, CommandError> expectedResult =
                ResultK.Err(new CommandError(11111L, "validation-error-text"));
        ResultK<CommandProcessed, CommandError> actual = splitCommandRule.process(update);
        assertNull(actual.getResult());
        assertEquals(expectedResult.getError().chatId(), actual.getError().chatId());
        assertEquals(expectedResult.getError().message(), actual.getError().message());
        verify(splitCommandValidator, times(1)).validate(eq(update));
        verify(splitCommandService, times(0)).split(any(SplitCommandValidated.class));
    }

    @Test
    void process_SplitException_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        SplitCommandValidated validatedMock = new SplitCommandValidated();
        validatedMock.setChatId(11111L);
        validatedMock.setFromUsername("user_1");
        validatedMock.setAmount(BigDecimal.valueOf(100.0));
        validatedMock.setDescription("test-description");
        ResultK<SplitCommandValidated, ValidationError> validatedResultMock = ResultK.Ok(validatedMock);
        when(splitCommandValidator.validate(update)).thenReturn(validatedResultMock);

        ResultK<CommandProcessed, CommandError> processedResultMock =
                ResultK.Err(new CommandError(11111L, "error-text"));
        when(splitCommandService.split(validatedMock)).thenReturn(processedResultMock);

        ResultK<CommandProcessed, CommandError> actual = splitCommandRule.process(update);
        assertNull(actual.getResult());
        assertEquals(processedResultMock.getError().chatId(), actual.getError().chatId());
        assertEquals(processedResultMock.getError().message(), actual.getError().message());
        verify(splitCommandValidator, times(1)).validate(eq(update));
        verify(splitCommandService, times(1)).split(eq(validatedMock));
    }
}
