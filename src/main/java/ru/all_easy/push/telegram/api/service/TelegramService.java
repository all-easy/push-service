package ru.all_easy.push.telegram.api.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.all_easy.push.common.ClientApi;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.common.client.model.SetWebhookInfo;
import ru.all_easy.push.telegram.api.client.TelegramFeignClient;
import ru.all_easy.push.telegram.api.client.model.SendMessageRequest;
import ru.all_easy.push.telegram.api.client.model.SetWebhookRequest;

@Service
public class TelegramService implements ClientApi {

    private final TelegramFeignClient telegramFeignClient;
    private final TelegramConfig telegramConfig;

    private final Logger logger = LoggerFactory.getLogger(TelegramService.class);

    public TelegramService(TelegramFeignClient telegramFeignClient, TelegramConfig telegramConfig) {
        this.telegramFeignClient = telegramFeignClient;
        this.telegramConfig = telegramConfig;
    }

    @PostConstruct
    void init() {
        setWebhook(new SetWebhookInfo("", telegramConfig.dropPendingUpdates(), ""))
                .flatMap(removeHookResult -> {
                    logger.info("Remove WebHook: {}", removeHookResult);
                    return setWebhook(new SetWebhookInfo(
                            telegramConfig.hook().fullHookUrl(),
                            telegramConfig.dropPendingUpdates(),
                            telegramConfig.hook().secret()));
                })
                .doOnSuccess(setWebHookResult -> logger.info(
                        "Set WebHook: {}, {}",
                        setWebHookResult,
                        telegramConfig.hook().fullHookUrl()))
                .subscribe();
    }

    @Override
    public Mono<String> setWebhook(SetWebhookInfo info) {
        SetWebhookRequest request = new SetWebhookRequest(info.url(), info.dropPendingUpdates(), info.secret());
        return telegramFeignClient.setWebhook(request);
    }

    @Override
    public Mono<String> sendMessage(SendMessageInfo info) {
        return telegramFeignClient
                .sendMessage(new SendMessageRequest(
                        info.chatId(), info.replayId(), info.text(), info.parseMode(), info.replayMarkup()))
                .doOnSuccess(logger::info)
                .doOnError(error -> logger.error("Error during sending message occurred: {}", error.getMessage()));
    }
}
