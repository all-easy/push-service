package ru.all_easy.push.room.controller.model;

import ru.all_easy.push.optimize.OweInfo;

import java.util.Set;

public record RoomResponse(
        String token,
        String title,
        Set<RoomUser> users,
        Set<OweInfo> oweInfos
) {
}
