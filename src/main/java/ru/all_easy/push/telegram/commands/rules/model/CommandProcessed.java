package ru.all_easy.push.telegram.commands.rules.model;

import ru.all_easy.push.common.client.model.ReplayMarkup;

public record CommandProcessed(Long chatId, String message, ReplayMarkup allButtons) {

    public CommandProcessed(Long chatId, String message) {
        this(chatId, message, null);
    }
}
