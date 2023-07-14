package ru.all_easy.push.telegram.commands.rules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.common.UnitTest;
import ru.all_easy.push.telegram.api.controller.model.*;
import ru.all_easy.push.telegram.commands.CommandsContextService;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.service.SplitCommandService;
import ru.all_easy.push.telegram.commands.validators.SplitCommandValidator;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;

/**
 * Unit testing for the split command rule used in the CommandContextService,
 * i.e. apply() and process() methods
 *
 * @see SplitCommandRule
 * @see CommandsContextService
 */
class SplitCommandRuleTest extends UnitTest {
    @InjectMocks
    SplitCommandRule splitCommandRule;

    @Mock
    SplitCommandValidator splitCommandValidator;

    @Mock
    SplitCommandService splitCommandService;

    @Test
    void applyPositiveTest_CommandWithNoArgs() {

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
    void applyPositiveTest_CommandWithAmountOnly() {

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
    void applyPositiveTest_CommandWithAmountAndDescription() {

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
    void applyPositiveTest_CommandWithAmountAndDescriptionAndPercent() {

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
    void applyNegativeTest_WrongCommand() {

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
    void processPositiveTest_CommandWithAmountAndDescription() {

        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(11111L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("test-description");
        ResultK<SplitCommandValidated, ValidationError> validatedResult = ResultK.Ok(validated);

        when(splitCommandValidator.validate(update)).thenReturn(validatedResult);

        ResultK<CommandProcessed, CommandError> expectedResult =
                ResultK.Ok(new CommandProcessed(11111L, "response-text"));

        when(splitCommandService.split(validated)).thenReturn(expectedResult);

        ResultK<CommandProcessed, CommandError> actual = splitCommandRule.process(update);

        assertNull(actual.getError());
        assertEquals(expectedResult.getResult().chatId(), actual.getResult().chatId());
        assertEquals(expectedResult.getResult().message(), actual.getResult().message());

        verify(splitCommandValidator, times(1)).validate(eq(update));
        verify(splitCommandService, times(1)).split(eq(validated));
    }

    @Test
    void processNegativeTest_ValidatorException() {

        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        ResultK<SplitCommandValidated, ValidationError> validatedResult =
                ResultK.Err(new ValidationError("validation-error-text"));

        when(splitCommandValidator.validate(update)).thenReturn(validatedResult);

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
    void processNegativeTest_SplitException() {

        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(11111L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("test-description");
        ResultK<SplitCommandValidated, ValidationError> validatedResult = ResultK.Ok(validated);

        when(splitCommandValidator.validate(update)).thenReturn(validatedResult);

        ResultK<CommandProcessed, CommandError> processedResultMock =
                ResultK.Err(new CommandError(11111L, "error-text"));

        when(splitCommandService.split(validated)).thenReturn(processedResultMock);

        ResultK<CommandProcessed, CommandError> actual = splitCommandRule.process(update);

        assertNull(actual.getResult());
        assertEquals(processedResultMock.getError().chatId(), actual.getError().chatId());
        assertEquals(processedResultMock.getError().message(), actual.getError().message());

        verify(splitCommandValidator, times(1)).validate(eq(update));
        verify(splitCommandService, times(1)).split(eq(validated));
    }
}
