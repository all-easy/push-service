package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;

@Service
public class StartCommand implements CommandRule {

    @Override
    public boolean apply(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }
        return update.message().text().contains(Commands.START.getCommand());
    }

    @Override
    public Mono<ResultK<CommandProcessed, CommandError>> process(Update update) {
        var message =
                """
        ```
        Hi! I help you manage expenses with your friends and reduce money transfer commissions

        Start with following steps:
        1) Add bot to chat group
        2) Ask all chat participants to call /addme command
        3) Call /currency command in chat and choose currency or skip this step
        4) Add your first expense
        5) Enjoy :)

        More information here -> /help
        ```
        """;
        return Mono.just(ResultK.Ok(new CommandProcessed(update.message().chat().id(), message)));
    }
}
