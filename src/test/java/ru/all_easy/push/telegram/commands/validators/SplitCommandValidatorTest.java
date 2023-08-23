package ru.all_easy.push.telegram.commands.validators;

import static org.junit.jupiter.api.Assertions.*;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.common.UnitTest;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.helper.PushParser;
import ru.all_easy.push.telegram.api.controller.model.*;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

/**
 * Unit testing for the validation of the input Update object,
 * for further processing the split command.
 *
 * @see SplitCommandValidator
 * @see SplitCommandValidated
 * @see Update
 */
class SplitCommandValidatorTest extends UnitTest {
    @InjectMocks
    SplitCommandValidator splitCommandValidator;

    @Spy
    PushParser pushParser = new PushParser(new MathHelper(new DoubleEvaluator()));

    @Test
    void validatePositiveTest_UpdateWithAmountAndDescription() {

        // TEST DATA

        Message message = new Message(
                1,
                new User(1L, false, "User 1 Full Name", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        // EXPECT

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(100.0));
        expectedValidated.setDescription("description");

        // RESULT

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validatePositiveTest_UpdateWithAmountOnly() {

        // TEST DATA

        Message message = new Message(
                1,
                new User(1L, false, "User 1 Full Name", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        // EXPECT

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(100.0));
        expectedValidated.setDescription("");

        // RESULT

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validatePositiveTest_UpdateWithAmountAndPercent() {

        // TEST DATA

        Message message = new Message(
                1,
                new User(1L, false, "User 1 Full Name", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 10%",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        // EXPECT

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(110.0));
        expectedValidated.setDescription("");

        // RESULT

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validatePositiveTest_UpdateWithAmountAndPercentAndDescription() {

        // TEST DATA

        Message message = new Message(
                1,
                new User(1L, false, "User 1 Full Name", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 10% description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        // EXPECT

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(110.0));
        expectedValidated.setDescription("description");

        // RESULT

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validatePositiveTest_UpdateWithAmountAndPercentTwoWordsDescription() {

        // TEST DATA

        Message message = new Message(
                1,
                new User(1L, false, "User 1 Full Name", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 10% description1 description2",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        // EXPECT

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(110.0));
        expectedValidated.setDescription("description1 description2");

        // RESULT

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validateNegativeTest_UpdateWithNoCommandArguments() {

        // TEST DATA

        Message message = new Message(
                1,
                new User(1L, false, "User 1 Full Name", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));

        Update update = new Update(null, message);

        // EXPECT

        ValidationError expectedError = new ValidationError(
                AnswerMessageTemplate.INCORRECT_FORMAT.getMessage().replace("/push", "/split"));

        // RESULT

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getResult());
        assertEquals(expectedError.message(), validated.getError().message());
    }
}
