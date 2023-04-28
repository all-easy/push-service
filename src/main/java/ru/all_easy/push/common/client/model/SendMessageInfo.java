package ru.all_easy.push.common.client.model;

import java.io.Serializable;

public record SendMessageInfo(
    Long chatId,
    String text,
    String parseMode
) implements Serializable {
}
