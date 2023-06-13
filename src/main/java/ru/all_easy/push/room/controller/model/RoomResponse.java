package ru.all_easy.push.room.controller.model;

import java.util.Set;
import ru.all_easy.push.optimize.OweInfo;

public record RoomResponse(String token, String title, Set<RoomUser> users, Set<OweInfo> oweInfos) {}
