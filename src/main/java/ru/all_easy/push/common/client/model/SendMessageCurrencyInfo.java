package ru.all_easy.push.common.client.model;

import ru.all_easy.push.telegram.api.client.model.InlineKeyboard;

public record SendMessageCurrencyInfo(
        Long chatId,
        String text,
        InlineKeyboard replyMarkup
) implements SendMessage {
}
