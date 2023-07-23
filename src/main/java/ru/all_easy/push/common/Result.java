package ru.all_easy.push.common;

import ru.all_easy.push.common.client.model.ReplayMarkup;

public interface Result {

    Long chatId();

    Integer replayToId();

    String message();

    ReplayMarkup replayMarkup();
}
