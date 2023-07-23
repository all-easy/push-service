package ru.all_easy.push.user.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    @Query("SELECT * FROM t_user tu WHERE tu.uid = :uid")
    Mono<UserEntity> findUserEntity(String uid);

    @Query(
            "SELECT * FROM t_user tu JOIN room_t_user rtu ON tu.uid = rtu.t_user_uid WHERE tu.username = :username AND rtu.room_token = :roomToken")
    Mono<UserEntity> findUserInRoomByUsername(String roomToken, String username);
}
