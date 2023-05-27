package ru.all_easy.push.telegram.commands.rules.model;

import ru.all_easy.push.telegram.api.client.model.InlineKeyboard;

public record CommandProcessed(
    String message,
    InlineKeyboard allButtons
) {

    public CommandProcessed(String message) {
        this(message, null);
    }
}
