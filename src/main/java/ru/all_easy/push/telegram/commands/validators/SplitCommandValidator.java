package ru.all_easy.push.telegram.commands.validators;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.helper.PushParser;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;

@Component
public class SplitCommandValidator {
    private final PushParser pushHelper;

    public SplitCommandValidator(PushParser pushHelper) {
        this.pushHelper = pushHelper;
    }

    public ResultK<SplitCommandValidated, ValidationError> validate(Update update) {
        String message = update.message().text();
        if (message == null || message.isEmpty() || message.isBlank()) {
            // return error
        }

        // check "test".split(" ");
        List<String> messageParts = Arrays.stream(message.split(" ")).toList();
        if (messageParts.size() == 1) {
            // Must be only expense amount or math expression
            // return all group members
            // or return an error if not amount of math expression
        }

        // if > 1: many variations
        // for (String messagePart : messageParts) {}
        // verify each part with methods from helper

        // amount, mathExpression, percent, usernames, from, chatId, title

        return null;
    }
}
