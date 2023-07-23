package ru.all_easy.push.telegram.commands.rules;

import reactor.core.publisher.Mono;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.api.controller.model.Update;

public interface CommandRule {

    boolean apply(Update update);

    Mono<ResultK> process(Update update);
}
