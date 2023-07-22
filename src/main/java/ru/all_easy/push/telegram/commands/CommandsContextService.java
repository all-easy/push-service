package ru.all_easy.push.telegram.commands;

import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.rules.CommandRule;

@Service
public class CommandsContextService {

    private final TelegramService telegramService;
    private final List<CommandRule> commands;

    public CommandsContextService(TelegramService telegramService, List<CommandRule> commands) {
        this.telegramService = telegramService;
        this.commands = commands;
    }

    public Mono<Void> process(Update update) {
        return Flux.fromIterable(commands)
                .filter(it -> it.apply(update))
                .next()
                .flatMap(rule -> rule.process(update))
                .flatMap(result -> {
                    if (result.hasError()) {
                        var chatId = result.getError().chatId();
                        return telegramService.sendMessage(new SendMessageInfo(chatId, result.getError().message(), ParseMode.MARKDOWN.getMode()));
                    } else {
                        var chatId = result.getResult().chatId();
                        return telegramService.sendMessage(new SendMessageInfo(
                                chatId,
                                result.getResult().replayToId(),
                                result.getResult().message(),
                                ParseMode.MARKDOWN.getMode(),
                                result.getResult().replayMarkup()));
                    }
                }).then();
    }
}
