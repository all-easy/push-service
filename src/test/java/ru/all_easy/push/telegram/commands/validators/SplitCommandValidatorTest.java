package ru.all_easy.push.telegram.commands.validators;

import static org.junit.jupiter.api.Assertions.*;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.helper.PushParser;
import ru.all_easy.push.telegram.api.controller.model.*;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

@ExtendWith(MockitoExtension.class)
class SplitCommandValidatorTest {
    @InjectMocks
    SplitCommandValidator splitCommandValidator;

    @Spy
    PushParser pushParser = new PushParser(new MathHelper(new DoubleEvaluator()));

    @Test
    void validate_AmountDescription_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(100.0));
        expectedValidated.setDescription("description");

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validate_Amount_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(100.0));
        expectedValidated.setDescription("");

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validate_AmountPercent_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 10%",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(110.0));
        expectedValidated.setDescription("");

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validate_AmountPercentDescription_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 10% description",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(110.0));
        expectedValidated.setDescription("description");

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validate_AmountPercentTwoWordsDescription_Positive() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split 100 10% description1 description2",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        SplitCommandValidated expectedValidated = new SplitCommandValidated();
        expectedValidated.setChatId(11111L);
        expectedValidated.setFromUsername("user_1");
        expectedValidated.setAmount(BigDecimal.valueOf(110.0));
        expectedValidated.setDescription("description1 description2");

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getError());
        assertEquals(expectedValidated.getChatId(), validated.getResult().getChatId());
        assertEquals(expectedValidated.getFromUsername(), validated.getResult().getFromUsername());
        assertEquals(expectedValidated.getAmount(), validated.getResult().getAmount());
        assertEquals(expectedValidated.getDescription(), validated.getResult().getDescription());
    }

    @Test
    void validate_NoCommandArguments_Negative() {
        Message message = new Message(
                1,
                new User(1L, false, "user_1_nickname", "user_1"),
                new Chat(11111L, "group", null, "room_title_1"),
                1686759117L,
                "/split",
                null,
                Collections.singletonList(new MessageEntity("bot_command", 0, null)));
        Update update = new Update(null, message);

        ValidationError expectedError = new ValidationError(AnswerMessageTemplate.INCORRECT_FORMAT_SPLIT.getMessage());

        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        assertNull(validated.getResult());
        assertEquals(expectedError.message(), validated.getError().message());
    }
}
