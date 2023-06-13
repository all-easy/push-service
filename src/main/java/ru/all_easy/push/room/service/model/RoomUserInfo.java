package ru.all_easy.push.room.service.model;

import ru.all_easy.push.shape.repository.Shape;

public record RoomUserInfo(
        String username,
        String uid,
        Shape shape
) {
}
