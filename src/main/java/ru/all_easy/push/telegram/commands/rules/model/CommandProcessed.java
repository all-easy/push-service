package ru.all_easy.push.telegram.commands.rules.model;

import ru.all_easy.push.common.Result;
import ru.all_easy.push.common.client.model.ReplayMarkup;

public record CommandProcessed(Long chatId, Integer replayToId, String message, ReplayMarkup replayMarkup)
        implements Result {

    public CommandProcessed(Long chatId, String message) {
        this(chatId, null, message, null);
    }

    public CommandProcessed(Long chatId, String message, ReplayMarkup replayMarkup) {
        this(chatId, null, message, replayMarkup);
    }

    public CommandProcessed(Long chatId, Integer replayId, String message) {
        this(chatId, replayId, message, null);
    }
}
