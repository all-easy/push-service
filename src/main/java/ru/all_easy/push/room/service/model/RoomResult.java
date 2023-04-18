package ru.all_easy.push.room.service.model;

import ru.all_easy.push.optimize.OweInfo;

import java.util.Set;

public record RoomResult(
        String owner,
        String token,
        String title,
        Set<RoomUserInfo> users,
        Set<OweInfo> oweInfos
) {
}
