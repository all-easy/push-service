package ru.all_easy.push.room.controller.model;

import ru.all_easy.push.room.repository.model.RoomStatus;

public record UserRoom(
        String token,
        String name,
        RoomStatus status
) {
}
