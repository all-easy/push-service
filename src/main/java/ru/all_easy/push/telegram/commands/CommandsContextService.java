package ru.all_easy.push.telegram.commands;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.rules.CommandRule;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;

import java.util.List;
import java.util.Optional;

@Service
public class CommandsContextService {

    private  final TelegramService telegramService;
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

        Optional<CommandRule> rule = commands.stream()
                .filter(it -> it.apply(update))
                .findFirst();
        if (rule.isPresent()) {
            var chatId = update.message().chat().id();
            ResultK<CommandProcessed, CommandError> processed = rule.get().process(update);
            if (processed.hasError()) {
                sendMessage(new SendMessageInfo(chatId, processed.getError().message(), ParseMode.MARKDOWN.getMode()));
            } else {
                sendMessage(new SendMessageInfo(chatId, processed.getResult().message(), ParseMode.MARKDOWN.getMode()));
            }
        }
    }

    private void sendMessage(SendMessageInfo message) {
        telegramService.sendMessage(message);
    }

}
