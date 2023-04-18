package ru.all_easy.push.room.controller.model;

import ru.all_easy.push.shape.repository.Shape;

public record RoomRequest(
        String title,
        Shape shape
) {
}
