package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.client.model.ForceReply;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;

@Service
public class PushForceReplayRule implements CommandRule {

    @Override
    public boolean apply(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }

        if (!update.message().text().contains(Commands.PUSH.getCommand())) {
            return false;
        }

        return update.message().text().split(" ").length == 1 &&
                (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                        || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        return ResultK.Ok(new CommandProcessed(
                update.message().chat().id(),
                update.message().messageId(),
                "/push",
                new ForceReply(
                        true,
                        "@username amount",
                        true
        )));
    }
}
