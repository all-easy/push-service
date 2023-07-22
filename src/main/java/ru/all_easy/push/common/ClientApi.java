package ru.all_easy.push.common;

import reactor.core.publisher.Mono;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.common.client.model.SetWebhookInfo;

public interface ClientApi {

    Mono<String> setWebhook(SetWebhookInfo info);

    Mono<String> sendMessage(SendMessageInfo info);
}
