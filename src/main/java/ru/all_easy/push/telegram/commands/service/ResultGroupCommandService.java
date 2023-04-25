package ru.all_easy.push.telegram.commands.service;

import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.controller.model.Update;

public interface ResultGroupCommandService {
    SendMessageInfo getResult(Update update);
}
