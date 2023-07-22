package ru.all_easy.push.room_user.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoomUserRepository extends R2dbcRepository<RoomUserEntity, Long> {

    Mono<RoomUserEntity> findByUserIdAndRoomId(String userId, String roomId);

    Flux<RoomUserEntity> findByUserId(String userId);
}
