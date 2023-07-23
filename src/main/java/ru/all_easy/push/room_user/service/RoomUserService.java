package ru.all_easy.push.room_user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.repository.model.RoomStatus;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.room_user.repository.RoomUserRepository;
import ru.all_easy.push.user.repository.UserEntity;

@Service
public class RoomUserService {

    private final RoomUserRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(RoomUserService.class);

    public RoomUserService(RoomUserRepository repository) {
        this.repository = repository;
    }

    public Mono<RoomUserEntity> findByUserAndRoom(UserEntity user, RoomEntity room) {
        return repository
                .findByUserUidAndRoomToken(user.getUid(), room.getToken())
                .doOnNext(roomUser -> {
                    if (roomUser == null) {
                        logger.info("RoomUser not found, username: {}", user.getUsername());
                    }
                });
    }

    public Flux<RoomUserEntity> findByUser(UserEntity user) {
        return repository.findByUserUid(user.getUid()).collectList().flatMapMany(roomUsers -> {
            if (roomUsers.isEmpty()) {
                logger.info("RoomUsers are empty, username: {}", user.getUsername());
            }
            return Flux.fromIterable(roomUsers);
        });
    }

    public Mono<RoomUserEntity> enterRoom(String uid, String token) {
        return repository.save(
                new RoomUserEntity().setUserUid(uid).setRoomToken(token).setStatus(RoomStatus.ACTIVE));
    }
}
