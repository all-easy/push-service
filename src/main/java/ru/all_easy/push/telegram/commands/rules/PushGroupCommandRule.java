package ru.all_easy.push.telegram.commands.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.service.PushGroupCommandService;

@Service
public class PushGroupCommandRule implements CommandRule {
    @Autowired
    PushGroupCommandService pushGroupCommandService;

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.PUSH.getCommand())
                && (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public SendMessageInfo process(Update update) {
        return pushGroupCommandService.getResult(update);
    }
}
