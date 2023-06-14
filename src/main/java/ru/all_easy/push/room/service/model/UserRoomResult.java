package ru.all_easy.push.room.service.model;

import ru.all_easy.push.room.repository.model.RoomStatus;

public record UserRoomResult(String token, String name, RoomStatus status) {}
