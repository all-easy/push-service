package ru.all_easy.push.room.service.model;

import java.util.Set;
import ru.all_easy.push.optimize.OweInfo;

public record RoomResult(String owner, String token, String title, Set<RoomUserInfo> users, Set<OweInfo> oweInfos) {}
