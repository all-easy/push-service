package ru.all_easy.push.telegram.commands.rules.model;

import ru.all_easy.push.telegram.api.client.model.InlineKeyboard;

public record CommandProcessed(
    String message,
    Long chatId,
    InlineKeyboard allButtons
) {

    public CommandProcessed(String message, Long chatId) {
        this(message, chatId,null);
    }
}
