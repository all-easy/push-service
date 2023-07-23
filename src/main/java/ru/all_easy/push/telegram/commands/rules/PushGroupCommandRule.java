package ru.all_easy.push.telegram.commands.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.service.PushGroupCommandService;
import ru.all_easy.push.telegram.commands.validators.PushCommandValidator;
import ru.all_easy.push.telegram.commands.validators.model.PushCommandValidated;

@Service
public class PushGroupCommandRule implements CommandRule {
    private final PushGroupCommandService pushGroupCommandService;
    private final PushCommandValidator pushCommandValidator;

    private static final Logger logger = LoggerFactory.getLogger(PushGroupCommandRule.class);

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
                && update.message().replayToMessage().text().contains(Commands.PUSH.getCommand());

        var groupSuperGroup = update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType());

        return (pushCommand || replay) && groupSuperGroup;
    }

    @Override
    public Mono<ResultK> process(Update update) {
        var validated = pushCommandValidator.validate(update);
        if (validated.hasError()) {
            return Mono.just(ResultK.Err(new CommandError(
                    update.message().chat().id(), validated.getError().message())));
        }

        var pushCommandResult = (PushCommandValidated) validated.getResult();

        return pushGroupCommandService
                .push(pushCommandResult)
                .flatMap(result -> {
                    if (result.hasError()) {
                        return Mono.just(ResultK.Err(new CommandError(
                                update.message().chat().id(), result.getError().message())));
                    }
                    return Mono.just(ResultK.Ok(new CommandProcessed(
                            update.message().chat().id(), result.getResult().message())));
                })
                .onErrorResume(ex -> {
                    logger.error(ex.getMessage());
                    return Mono.just(ResultK.Err(new CommandError(
                            pushCommandResult.chatId(), "Something Went wrong, time to check logs :)")));
                });
    }
}
