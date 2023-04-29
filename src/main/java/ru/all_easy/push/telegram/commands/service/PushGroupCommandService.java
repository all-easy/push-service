package ru.all_easy.push.telegram.commands.service;

import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;

public interface PushGroupCommandService {
    SendMessageInfo getResult(Update update);
}
