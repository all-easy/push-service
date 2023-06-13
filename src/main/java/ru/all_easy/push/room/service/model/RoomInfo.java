package ru.all_easy.push.room.service.model;

import ru.all_easy.push.shape.repository.Shape;

public record RoomInfo(String ownerUid, String title, String token, Shape shape) {}
