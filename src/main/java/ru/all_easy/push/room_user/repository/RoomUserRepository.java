package ru.all_easy.push.room_user.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.all_easy.push.room.repository.model.RoomStatus;

public interface RoomUserRepository extends R2dbcRepository<RoomUserEntity, Long> {

    Mono<RoomUserEntity> findByUserUidAndRoomToken(String userId, String roomId);

    Flux<RoomUserEntity> findByUserUid(String userId);

    @Query(
            "INSERT INTO room_t_user (room_token, t_user_uid, status) VALUES (:token, :uid, :roomStatus) ON CONFLICT DO NOTHING")
    Mono<RoomUserEntity> save(String uid, String token, RoomStatus roomStatus);
}
