package ru.all_easy.push.room_user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.room_user.repository.RoomUserRepository;
import ru.all_easy.push.user.repository.UserEntity;

import java.util.List;

@Service
public class RoomUserService {

    private final RoomUserRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(RoomUserService.class);

    public RoomUserService(RoomUserRepository repository) {
        this.repository = repository;
    }

    public RoomUserEntity findByUserAndRoom(UserEntity user, RoomEntity room) {
        RoomUserEntity roomUser = repository.findByUserAndRoom(user, room);
        if (roomUser == null) {
            logger.info("RoomUser not found, username: {}", user.getUsername());
        }

        return roomUser;
    }

    public List<RoomUserEntity> findByUser(UserEntity user) {
        List<RoomUserEntity> roomUsers = repository.findByUser(user);
        if (roomUsers.isEmpty()) {
            logger.info("RoomUsers are empty, username: {}", user.getUsername());
        }

        return roomUsers;
    }
}
