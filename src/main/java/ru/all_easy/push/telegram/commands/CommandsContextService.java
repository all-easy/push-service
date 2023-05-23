package ru.all_easy.push.telegram.commands;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.rules.CommandRule;

import java.util.List;

@Service
public class CommandsContextService {

    private final TelegramService telegramService;
    private final List<CommandRule> commands;

    public CommandsContextService(TelegramService telegramService,
                                  List<CommandRule> commands) {
        this.telegramService = telegramService;
        this.commands = commands;
    }

    public void process(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return;
        }

        commands.stream()
            .filter(it -> it.apply(update))
            .findFirst()
            .map(commandRule -> commandRule.process(update))
            .ifPresent(result -> {
                var chatId = update.message().chat().id();
                if (result.hasError()) {
                    sendMessage(new SendMessageInfo(chatId, result.getError().message(), ParseMode.MARKDOWN.getMode()));
                } else {
                    sendMessage(new SendMessageInfo(chatId, result.getResult().message(), ParseMode.MARKDOWN.getMode()));
                }
            });
    }

    private void sendMessage(SendMessageInfo message) {
        telegramService.sendMessage(message);
    }

}
