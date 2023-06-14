package ru.all_easy.push.room.service.model;

import ru.all_easy.push.shape.repository.Shape;

public record RoomJoinInfo(String uid, String username, Shape shape, String roomToken) {}
