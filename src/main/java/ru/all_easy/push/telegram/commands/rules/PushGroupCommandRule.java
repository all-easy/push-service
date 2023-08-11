package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.service.PushGroupCommandService;
import ru.all_easy.push.telegram.commands.validators.PushCommandValidator;

@Service
public class PushGroupCommandRule implements CommandRule {
    private final PushGroupCommandService pushGroupCommandService;
    private final PushCommandValidator pushCommandValidator;

    public PushGroupCommandRule(
            PushGroupCommandService pushGroupCommandService, PushCommandValidator pushCommandValidator) {
        this.pushGroupCommandService = pushGroupCommandService;
        this.pushCommandValidator = pushCommandValidator;
    }

    @Override
    public boolean apply(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }

        var pushCommand = update.message().text().contains(Commands.PUSH.getCommand());
        if (update.message().text().split(" ").length <= 1 && pushCommand) {
            return false;
        }

        var replay = update.message().replayToMessage() != null
                && update.message().replayToMessage().text() != null
                && update.message().replayToMessage().text().contains(Commands.PUSH.getCommand());

        var groupSuperGroup = update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType());

        return (pushCommand || replay) && groupSuperGroup;
    }

    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        var validated = pushCommandValidator.validate(update);
        if (validated.hasError()) {
            return ResultK.Err(new CommandError(
                    update.message().chat().id(), validated.getError().message()));
        }

        var result = pushGroupCommandService.push(validated.getResult());
        if (result.hasError()) {
            return ResultK.Err(new CommandError(
                    update.message().chat().id(), result.getError().message()));
        }

        return ResultK.Ok(new CommandProcessed(update.message().chat().id(), result.getResult()));
    }
}
