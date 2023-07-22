package ru.all_easy.push.telegram.commands.service;

import reactor.core.publisher.Mono;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.commands.service.model.PushCommandServiceError;
import ru.all_easy.push.telegram.commands.validators.model.PushCommandValidated;

public interface PushGroupCommandService {
    Mono<ResultK<String, PushCommandServiceError>> push(PushCommandValidated validated);
}
