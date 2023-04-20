package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;

@Service
public class HelpCommandRule implements CommandRule {

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.HELP.getCommand());
    }

    @Override
    public SendMessageInfo process(Update update) {
        String message = "[Here you can find info about bot and how to use it](https://all-easy.notion.site/all-easy/Push-Money-Bot-c86ff025dd144f0aa67fee649e9fe538)";
        return new SendMessageInfo(
                update.message().chat().id(),
                message, ParseMode.MARKDOWN.getMode());
    }
    
}
