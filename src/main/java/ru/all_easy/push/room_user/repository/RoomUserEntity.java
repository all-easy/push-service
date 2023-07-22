package ru.all_easy.push.room_user.repository;

import org.springframework.data.annotation.Id;
import ru.all_easy.push.room.repository.model.RoomStatus;

public class RoomUserEntity {

    @Id
    private Long id;

    private String roomToken;

    private String userUid;
    private String roomId;
    private String userId;
    private RoomStatus status;

    public Long getId() {
        return id;
    }

    public RoomUserEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRoomToken() {
        return roomToken;
    }

    public RoomUserEntity setRoomToken(String roomToken) {
        this.roomToken = roomToken;
        return this;
    }

    public String getUserUid() {
        return userUid;
    }

    public RoomUserEntity setUserUid(String userUid) {
        this.userUid = userUid;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getUserId() {
        return userId;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public RoomUserEntity setStatus(RoomStatus roomStatus) {
        this.status = roomStatus;
        return this;
    }

    public RoomUserEntity setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public RoomUserEntity setUserId(String userId) {
        this.userId = userId;
        return this;
    }
}
