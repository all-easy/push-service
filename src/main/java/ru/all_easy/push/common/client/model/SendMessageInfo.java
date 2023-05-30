package ru.all_easy.push.common.client.model;

import ru.all_easy.push.telegram.api.client.model.InlineKeyboard;

public record SendMessageInfo(
    Long chatId,
    String text,
    String parseMode,
    InlineKeyboard keyboard) implements SendMessage {
    public SendMessageInfo(Long chatId, String text, String parseMode) {
        this(chatId, text, parseMode, null);
    }
}
