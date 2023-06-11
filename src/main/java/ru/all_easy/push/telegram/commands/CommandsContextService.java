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
        commands.stream()
            .filter(it -> it.apply(update))
            .findFirst()
            .map(commandRule -> commandRule.process(update))
            .ifPresent(result -> {
                if (result.hasError()) {
                    var chatId = result.getError().chatId();
                    sendMessage(new SendMessageInfo(chatId, result.getError().message(), ParseMode.MARKDOWN.getMode()));
                } else {
                    var chatId = result.getResult().chatId();
                    sendMessage(new SendMessageInfo(
                            chatId,
                            result.getResult().replayToId(),
                            result.getResult().message(),
                            ParseMode.MARKDOWN.getMode(),
                            result.getResult().replayMarkup()));
                }
            });
    }

    private void sendMessage(SendMessageInfo message) {
        telegramService.sendMessage(message);
    }

}
