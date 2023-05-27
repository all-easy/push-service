package ru.all_easy.push.telegram.commands.validators;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.common.UnitTest;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.helper.PushParser;
import ru.all_easy.push.telegram.api.controller.model.Chat;
import ru.all_easy.push.telegram.api.controller.model.Message;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.controller.model.User;
import ru.all_easy.push.telegram.commands.validators.model.PushCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PushCommandValidatorUnitTest extends UnitTest {

    @Spy
    private PushParser pushParser = new PushParser(new MathHelper(new DoubleEvaluator()));

    @InjectMocks
    private PushCommandValidator pushCommandValidator;

    @Test
    void incorrectCommandSize() {
        Update update = generateUpdate("fromUsername", "/push @username");
        ResultK<PushCommandValidated, ValidationError> validated = pushCommandValidator.validate(update);

        assertEquals(
            "Incorrect format \uD83E\uDD14, try like this: /push @username 123 name 18%",
            validated.getError().message());
    }

    @Test
    void incorrectToUsername() {
        Update update = generateUpdate("fromUsername", "/push 11+11 name1 name2 18%");

        ResultK<PushCommandValidated, ValidationError> validated = pushCommandValidator.validate(update);

        assertEquals(
                "Unrecognized username",
                validated.getError().message());
    }

    @Test
    void fromUsernameEqualsToUsername() {
        Update update = generateUpdate("username", "/push @username 111 name1 name2 18%");

        ResultK<PushCommandValidated, ValidationError> validated = pushCommandValidator.validate(update);

        assertEquals(
                "You can't push to yourself \uD83D\uDE22",
                validated.getError().message());
    }

    @Test
    void exceptionIfWrongMathExpression() {
        Update update = generateUpdate("username", "/push @toUsername name1 name2 18%");

        ResultK<PushCommandValidated, ValidationError> validated = pushCommandValidator.validate(update);

        assertEquals(
                "You typed wrong amount expression, example: 10+10+10",
                validated.getError().message());
    }

    @Test
    void successPushCommandLongNamePercent() {
        Update update = generateUpdate("fromUsername", "/push @toUsername 100+100 name1 name2 18%");

        ResultK<PushCommandValidated, ValidationError> validated = pushCommandValidator.validate(update);

        assertEquals(validated.getResult().getToUsername(), "toUsername");
        assertEquals(validated.getResult().getFromUsername(), "fromUsername");
        assertEquals(validated.getResult().getAmount(), BigDecimal.valueOf(236.0));
        assertEquals(validated.getResult().getName(), "name1 name2");
    }

    @Test
    void successPushCommandPercentLongName() {
        Update update = generateUpdate("fromUsername", "/push @toUsername 100+100 18% name1 name2 22 name3");

        ResultK<PushCommandValidated, ValidationError> validated = pushCommandValidator.validate(update);

        assertEquals(validated.getResult().getToUsername(), "toUsername");
        assertEquals(validated.getResult().getFromUsername(), "fromUsername");
        assertEquals(validated.getResult().getAmount(), BigDecimal.valueOf(236.0));
        assertEquals(validated.getResult().getName(), "name1 name2 22 name3");
    }

    @Test
    void successPushCommandLongName() {
        Update update = generateUpdate("fromUsername", "/push @toUsername 200 name1 name2 22 name3");

        ResultK<PushCommandValidated, ValidationError> validated = pushCommandValidator.validate(update);

        assertEquals(validated.getResult().getToUsername(), "toUsername");
        assertEquals(validated.getResult().getFromUsername(), "fromUsername");
        assertEquals(validated.getResult().getAmount(), BigDecimal.valueOf(200.0));
        assertEquals(validated.getResult().getName(), "name1 name2 22 name3");
    }

    private Update generateUpdate(String username, String message) {
        return new Update(1L,
            new Message(1L,
                    new User(1L, false, "a", username),
                    new Chat(1l, "type", "username", "ChatTitle"),
                    Instant.now().getEpochSecond(),
                    message, List.of()));
    }
}
