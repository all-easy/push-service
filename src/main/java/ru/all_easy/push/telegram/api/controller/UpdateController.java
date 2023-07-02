package ru.all_easy.push.telegram.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import ru.all_easy.push.common.AbstractAuthentication;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.CommandsContextService;

@RestController
@RequestMapping("/v1/api/telegram/")
public class UpdateController extends AbstractAuthentication {

    private static final Logger logger = LoggerFactory.getLogger(UpdateController.class);

    private final String secret;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        logger.error("400 Bad Request from Telegram", e);
    }

    private final CommandsContextService commandContextService;

    public UpdateController(
            @Value("${telegram.hook.secret}") String secret, CommandsContextService commandContextService) {
        this.secret = secret;
        this.commandContextService = commandContextService;
    }

    @PostMapping("/")
    public void postMethodName(
            @RequestHeader("X-Telegram-Bot-Api-Secret-Token") String secretToken, @RequestBody Update update) {
        if (!secretToken.equals(secret)) {
            logger.error("Header secret token not match for update: {}", update);
            return;
        }
        logger.info("Update received: {}", update);
        commandContextService.process(update);
    }
}
