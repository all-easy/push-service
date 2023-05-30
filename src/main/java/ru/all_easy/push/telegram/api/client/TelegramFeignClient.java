package ru.all_easy.push.telegram.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.all_easy.push.telegram.api.client.model.SendMessageRequest;
import ru.all_easy.push.telegram.api.client.model.SetWebhookRequest;

@FeignClient(name = "telegram", url = "${telegram.url}")
public interface TelegramFeignClient {

    @PostMapping("/setWebhook")
    String setWebhook(SetWebhookRequest request);
    
    @PostMapping("/sendMessage")
    String sendMessage(SendMessageRequest request);
}
