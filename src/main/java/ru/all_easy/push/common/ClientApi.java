package ru.all_easy.push.common;

import ru.all_easy.push.common.client.model.SendMessageCurrencyInfo;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.common.client.model.SetWebhookInfo;

public interface ClientApi {

    String setWebhook(SetWebhookInfo info);

    String sendMessage(SendMessageInfo info);
    String sendMessage(SendMessageCurrencyInfo info);

}
