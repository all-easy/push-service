package ru.all_easy.push.shape.repository;

import java.io.Serializable;

public class ShapeId implements Serializable {

    private String roomToken;
    private String userUid;

    public String getRoomToken() {
        return roomToken;
    }

    public String getUserUid() {
        return userUid;
    }

    public ShapeId setRoomToken(String roomToken) {
        this.roomToken = roomToken;
        return this;
    }

    public ShapeId setUserUid(String userUid) {
        this.userUid = userUid;
        return this;
    }
}
