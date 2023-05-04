package ru.all_easy.push.common.client.model;

public record SendMessageInfo(
    Long chatId,
    String text,
    String parseMode
) implements SendMessage {
}
