package ru.all_easy.push.telegram.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import ru.all_easy.push.common.AbstractAuthentication;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.supergroup.GroupToSuperGroupEvent;
import ru.all_easy.push.telegram.commands.CommandsContextService;

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

    private ApplicationEventPublisher applicationEventPublisher;

    public UpdateController(
            ApplicationEventPublisher applicationEventPublisher, CommandsContextService commandContextService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.commandContextService = commandContextService;
    }

    @PostMapping("/")
    public void postMethodName(@RequestBody Update update) {
        logger.info("Update received: {}", update);
        if (update.message().migrateFromChatId() != null && update.message().migrateToChatId() != null) {
            GroupToSuperGroupEvent event = new GroupToSuperGroupEvent(this, update);
            applicationEventPublisher.publishEvent(event);
        } else commandContextService.process(update);
    }
}
