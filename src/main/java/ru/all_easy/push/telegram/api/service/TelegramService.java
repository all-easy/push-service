package ru.all_easy.push.telegram.api.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.all_easy.push.common.ClientApi;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.common.client.model.SetWebhookInfo;
import ru.all_easy.push.telegram.api.client.TelegramFeignClient;
import ru.all_easy.push.telegram.api.client.model.SendMessageRequest;
import ru.all_easy.push.telegram.api.client.model.SetWebhookRequest;

@Service
public class TelegramService implements ClientApi {

    private final String hookUrl;
    private final boolean dropPendingUpdates;
    private final TelegramFeignClient telegramFeignClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramService.class);

    public TelegramService(@Value("${telegram.hook}") String hookUrl,
                           @Value("${telegram.drop_pending_updates}") boolean dropPendingUpdates,
                           TelegramFeignClient telegramFeignClient) {
        this.hookUrl = hookUrl;
        this.dropPendingUpdates = dropPendingUpdates;
        this.telegramFeignClient = telegramFeignClient;
    }

    @PostConstruct
    void init() {
        String removeHookResult = setWebhook(new SetWebhookInfo("", dropPendingUpdates));
        LOGGER.info("Remove WebHook: {}", removeHookResult);
        String setHookResult = setWebhook(new SetWebhookInfo(hookUrl, dropPendingUpdates));
        LOGGER.info("Set WebHook: {}, {}", setHookResult, hookUrl);
    }

    @Override
    public String setWebhook(SetWebhookInfo info) {
        SetWebhookRequest request = new SetWebhookRequest(info.url(), info.drop_pending_updates());
        String response = telegramFeignClient.setWebhook(request);
        LOGGER.info(response);

        return response;
    }

    @Override
    public String sendMessage(SendMessageInfo info) {
        return telegramFeignClient.sendMessage(new SendMessageRequest(info.chatId(), info.text(), info.parseMode()));
    }
}
