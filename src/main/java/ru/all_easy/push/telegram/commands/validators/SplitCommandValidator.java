package ru.all_easy.push.telegram.commands.validators;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.helper.PushParser;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

@Component
public class SplitCommandValidator {
    private final PushParser pushParser;

    public SplitCommandValidator(PushParser pushParser) {
        this.pushParser = pushParser;
    }

    public ResultK<SplitCommandValidated, ValidationError> validate(Update update) {
        String message = update.message().text();
        List<String> messageParts = Arrays.stream(message.split(" ")).toList();

        if (messageParts.size() < 2) {
            return ResultK.Err(new ValidationError(AnswerMessageTemplate.INCORRECT_FORMAT_SPLIT.getMessage()));
        }

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setFromUsername(update.message().from().username());
        validated.setChatId(update.message().chat().id());

        List<String> additionalQueryInfo = messageParts.subList(2, messageParts.size());
        validated.setDescription(pushParser.extractName(additionalQueryInfo));
        try {
            int percent = pushParser.extractPercent(additionalQueryInfo);
            BigDecimal calculatedAmount = pushParser.addPercentToMathExpression(messageParts.get(1), percent);
            validated.setAmount(calculatedAmount);
        } catch (IllegalArgumentException ex) {
            return ResultK.Err(new ValidationError(AnswerMessageTemplate.INCORRECT_MATH_EXPRESSION.getMessage()));
        }

        return ResultK.Ok(validated);
    }
}
