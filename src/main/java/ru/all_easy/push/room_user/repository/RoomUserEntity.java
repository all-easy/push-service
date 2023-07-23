package ru.all_easy.push.room_user.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.all_easy.push.room.repository.model.RoomStatus;

@Table("room_t_user")
public class RoomUserEntity {

    @Id
    private Long id;

    @Column("room_token")
    private String roomToken;

    @Column("t_user_uid")
    private String userUid;

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

    public RoomStatus getStatus() {
        return status;
    }

    public RoomUserEntity setStatus(RoomStatus roomStatus) {
        this.status = roomStatus;
        return this;
    }
}
