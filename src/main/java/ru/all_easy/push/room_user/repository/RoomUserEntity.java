package ru.all_easy.push.room_user.repository;

import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.repository.model.RoomStatus;
import ru.all_easy.push.user.repository.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "room_t_user")
public class RoomUserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_token")
    private String roomToken;

    @Column(name = "t_user_uid")
    private String userUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_token", referencedColumnName = "token")
    @MapsId(value = "roomToken")
    private RoomEntity room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "t_user_uid", referencedColumnName = "uid")
    @MapsId(value = "userUid")
    private UserEntity user;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    public RoomUserEntity() {

    }

    public RoomUserEntity(RoomEntity room, UserEntity user) {
        this.room = room;
        this.user = user;
        this.roomToken = room.getToken();
        this.userUid = user.getUid();
    }

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

    public RoomEntity getRoom() {
        return room;
    }

    public UserEntity getUser() {
        return user;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public RoomUserEntity setStatus(RoomStatus roomStatus) {
        this.status = roomStatus;
        return this;
    }

    public RoomUserEntity setRoom(RoomEntity room) {
        this.room = room;
        return this;
    }

    public RoomUserEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }
}
