package ru.all_easy.push.common.client.model;

public record SendMessageInfo(Long chatId, String text, String parseMode, ReplayMarkup replayMarkup) {
    public SendMessageInfo(Long chatId, String text, String parseMode) {
        this(chatId, text, parseMode, null);
    }
}
