package ru.all_easy.push.common.client.model;

public record SendMessageInfo(Long chatId, Integer replayId, String text, String parseMode, ReplayMarkup replayMarkup) {
    public SendMessageInfo(Long chatId, String text, String parseMode) {
        this(chatId, null, text, parseMode, null);
    }

    public SendMessageInfo(Long chatId, Integer replayId, String text, String parseMode) {
        this(chatId, replayId, text, parseMode, null);
    }
}
