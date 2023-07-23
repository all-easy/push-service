package ru.all_easy.push.telegram.commands.validators;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.helper.PushParser;
import ru.all_easy.push.telegram.api.controller.model.MessageEntity;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.validators.model.PushCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

@Component
public class PushCommandValidator {

    private static final String TEXT_MENTION = "text_mention";
    private final PushParser pushHelper;

    public PushCommandValidator(PushParser pushHelper) {
        this.pushHelper = pushHelper;
    }

    public ResultK validate(Update update) {
        var messageText = update.message().text();
        var chatId = update.message().chat().id();
        if (update.message().replayToMessage() != null) {
            messageText = update.message().replayToMessage().text() + " " + messageText;
        }

        var messageParts = Arrays.stream(messageText.split(" ")).toList();

        if (messageParts.size() < 3) {
            return ResultK.Err(new ValidationError(chatId, AnswerMessageTemplate.INCORRECT_FORMAT.getMessage()));
        }

        var validated = new PushCommandValidated();
        for (MessageEntity entity : update.message().entities()) {
            if (TEXT_MENTION.equals(entity.type()) && !entity.user().username().isEmpty()) {
                validated.setToUsername(entity.user().username());
            }
        }

        if (!messageParts.get(1).contains("@")) {
            return ResultK.Err(new ValidationError(chatId, AnswerMessageTemplate.UNRECOGNIZED_USERNAME.getMessage()));
        }

        validated.setToUsername(messageParts.get(1).replace("@", ""));

        var fromUsername = update.message().from().username();
        if (fromUsername.equals(validated.toUsername())) {
            return ResultK.Err(new ValidationError(chatId, AnswerMessageTemplate.YOURSELF_PUSH.getMessage()));
        }
        validated.setFromUsername(fromUsername);

        List<String> additionalQueryInfo = messageParts.subList(3, messageParts.size());
        validated.setName(pushHelper.extractName(additionalQueryInfo));
        try {
            int percent = pushHelper.extractPercent(additionalQueryInfo);
            BigDecimal calculatedAmount = pushHelper.addPercentToMathExpression(messageParts.get(2), percent);
            validated.setAmount(calculatedAmount);
        } catch (IllegalArgumentException ex) {
            return ResultK.Err(new ValidationError(chatId, AnswerMessageTemplate.INCORRECT_MATH_EXPRESSION.getMessage()));
        }

        validated.setChatId(update.message().chat().id());

        return ResultK.Ok(validated);
    }
}
