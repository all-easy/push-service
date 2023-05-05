package ru.all_easy.push.telegram.api.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.all_easy.push.common.ClientApi;
import ru.all_easy.push.common.client.model.SendMessageCurrencyInfo;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.common.client.model.SetWebhookInfo;
import ru.all_easy.push.telegram.api.client.TelegramFeignClient;
import ru.all_easy.push.telegram.api.client.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramService implements ClientApi {
    private final TelegramFeignClient telegramFeignClient;
    private final TelegramConfig telegramConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramService.class);

    public TelegramService(TelegramFeignClient telegramFeignClient, TelegramConfig telegramConfig) {
        this.telegramFeignClient = telegramFeignClient;
        this.telegramConfig = telegramConfig;
    }

    @PostConstruct
    void init() {
        String removeHookResult = setWebhook(new SetWebhookInfo("", telegramConfig.dropPendingUpdates()));
        LOGGER.info("Remove WebHook: {}", removeHookResult);
        String setHookResult = setWebhook(new SetWebhookInfo(telegramConfig.hook(), telegramConfig.dropPendingUpdates()));
        LOGGER.info("Set WebHook: {}, {}", setHookResult, telegramConfig.hook());
    }

    @Override
    public String setWebhook(SetWebhookInfo info) {
        SetWebhookRequest request = new SetWebhookRequest(info.url(), info.dropPendingUpdates());
        String response = telegramFeignClient.setWebhook(request);
        LOGGER.info(response);

        return response;
    }

    @Override
    public String sendMessage(SendMessageInfo info) {
        return telegramFeignClient.sendMessage(new SendMessageRequest(info.chatId(), info.text(), info.parseMode()));
    }

    @Override
    public String sendMessage(SendMessageCurrencyInfo info) {
        return telegramFeignClient.sendMessage(
                new SendMessageCurrencyRequest(info.chatId(), info.text(), info.replyMarkup()));
    }
}
