package ru.all_easy.push.telegram.commands.service;

import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;

public interface PushGroupCommandService {
    SendMessageInfo getResult(Update update);

    default SendMessageInfo validate(Long chatId, String[] messageParts) {
        if (messageParts.length < 3 || messageParts.length > 5) {
            String answerMessage =
                    "Incorrect format 🤔, try like this: /push @to <amount> <optional expense name> <optional amount%>";
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        }

        return null;
    }

    default RoomUserEntity findRoomUser(RoomEntity room, String username) {
        return room.getUsers().stream()
                .filter(entity -> entity.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
