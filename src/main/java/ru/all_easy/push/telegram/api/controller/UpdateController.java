package ru.all_easy.push.telegram.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.all_easy.push.common.AbstractAuthentication;
import ru.all_easy.push.common.client.model.SendMessage;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.CommandsContextService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/v1/api/telegram/${telegram.hook.secret}")
public class UpdateController extends AbstractAuthentication {

    private static final Logger logger = LoggerFactory.getLogger(UpdateController.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        logger.error("400 Bad Request from Telegram, {}", e);
    }

    private final CommandsContextService commandContextService;
    private final TelegramService telegramService;

    public UpdateController(CommandsContextService commandContextService,
                            TelegramService telegramService) {
        this.commandContextService = commandContextService;
        this.telegramService = telegramService;
    }

    @PostMapping("/")
    public void postMethodName(@RequestBody Update update) {
        logger.info("Update received: {}", update);

        if (update.callbackQuery() != null) {
            // TODO: update room in db with currency from callback
            // helper.updateRoomCurrency(chatId, currency)
            SendMessage message = new SendMessageInfo(
                    update.callbackQuery().message().chat().id(),
                    update.callbackQuery().data(), // chosen currency
                    ParseMode.MARKDOWN.getMode()
            );
            telegramService.sendMessage((SendMessageInfo) message);
        } else {
            commandContextService.process(update);
        }
    }

}
