package ru.all_easy.push.telegram.commands.service.model;

import ru.all_easy.push.common.Result;
import ru.all_easy.push.common.client.model.ReplayMarkup;

public record PushCommandServiceResult(
    Long chatId,
    Integer replayToId,
    String message,
    ReplayMarkup replayMarkup
) implements Result {
}
