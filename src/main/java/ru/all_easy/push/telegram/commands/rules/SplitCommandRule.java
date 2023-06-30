package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.service.SplitCommandService;
import ru.all_easy.push.telegram.commands.validators.SplitCommandValidator;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.commands.validators.model.ValidationError;

@Service
public class SplitCommandRule implements CommandRule {
    private final SplitCommandValidator splitCommandValidator;
    private final SplitCommandService splitCommandService;

    public SplitCommandRule(SplitCommandValidator splitCommandValidator, SplitCommandService splitCommandService) {
        this.splitCommandValidator = splitCommandValidator;
        this.splitCommandService = splitCommandService;
    }

    @Override
    public boolean apply(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }
        return update.message().text().contains(Commands.SPLIT.getCommand());
    }

    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        Long chatId = update.message().chat().id();
        ResultK<SplitCommandValidated, ValidationError> validated = splitCommandValidator.validate(update);
        if (validated.hasError()) {
            return ResultK.Err(new CommandError(
                    update.message().chat().id(), validated.getError().message()));
        }

        ResultK<CommandProcessed, CommandError> result = splitCommandService.split(validated.getResult());
        if (result.hasError()) {
            return ResultK.Err(new CommandError(
                    update.message().chat().id(), result.getError().message()));
        }

        return ResultK.Ok(new CommandProcessed(chatId, result.getResult().message()));
    }
}
