package ru.all_easy.push.telegram.api.client;

import org.springframework.web.bind.annotation.PostMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;
import ru.all_easy.push.telegram.api.client.config.FeignConfiguration;
import ru.all_easy.push.telegram.api.client.model.SendMessageRequest;
import ru.all_easy.push.telegram.api.client.model.SetWebhookRequest;

@ReactiveFeignClient(name = "telegram", url = "${telegram.url}", configuration = FeignConfiguration.class)
public interface TelegramFeignClient {

    @PostMapping("/setWebhook")
    Mono<String> setWebhook(SetWebhookRequest request);

    @PostMapping("/sendMessage")
    Mono<String> sendMessage(SendMessageRequest request);
}
