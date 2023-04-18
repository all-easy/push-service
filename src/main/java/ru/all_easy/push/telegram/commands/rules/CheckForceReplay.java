package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.ForceReplay;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.Commands;

@Service
public class CheckForceReplay implements CommandRule {

    private final TelegramService telegramService;

    public CheckForceReplay(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public boolean apply(Update update) {
        return Commands.REPLAY.getCommand().equals(update.message().text());
    }

    @Override
    public void process(Update update) {
        telegramService.sendMessage(new SendMessageInfo(
                update.message().chat().id(),
                "savelink",
                ParseMode.MARKDOWN.getMode(),
                new ForceReplay(
                        true,
                        "Example: https://google.com",
                        true
                )
        ));
    }
}
