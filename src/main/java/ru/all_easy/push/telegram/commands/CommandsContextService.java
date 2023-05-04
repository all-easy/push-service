package ru.all_easy.push.telegram.commands;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessage;
import ru.all_easy.push.common.client.model.SendMessageCurrencyInfo;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.client.TelegramFeignClient;
import ru.all_easy.push.telegram.api.client.model.InlineKeyboard;
import ru.all_easy.push.telegram.api.client.model.InlineKeyboardButton;
import ru.all_easy.push.telegram.api.client.model.SendMessageCurrencyRequest;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.rules.CommandRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CommandsContextService {

    private final TelegramService telegramService;
    private final List<CommandRule> commands;
    private final TelegramFeignClient telegramFeignClient;

    public CommandsContextService(TelegramService telegramService,
                                  List<CommandRule> commands,
                                  TelegramFeignClient telegramFeignClient) {
        this.telegramService = telegramService;
        this.commands = commands;
        this.telegramFeignClient = telegramFeignClient;
    }

    public void process(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return;
        }

            Optional<CommandRule> rule = commands.stream()
                    .filter(it -> it.apply(update))
                    .findFirst();
            if (rule.isPresent()) {
                SendMessage messageInfo = rule.get().process(update);
//                SendMessageInfo messageInfo = rule.get().process(update);
                sendMessage(messageInfo);
            }

    }

//    private void sendMessage(SendMessageInfo message) {
//        telegramService.sendMessage(message);
//    }

    private void sendMessage(SendMessage message) {
        if (message instanceof SendMessageInfo){
            telegramService.sendMessage((SendMessageInfo) message);
        }

        if (message instanceof SendMessageCurrencyInfo){
            telegramService.sendMessage((SendMessageCurrencyInfo) message);
        }
    }

}
