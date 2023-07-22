package ru.all_easy.push.user.repository;

import java.util.Set;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    @Query("SELECT user FROM UserEntity user LEFT JOIN user.rooms WHERE user.uid = :uid")
    Mono<UserEntity> findUserEntity(String uid);

    @Query(
            "SELECT user FROM UserEntity user JOIN RoomUserEntity roomUser ON user.uid = roomUser.userUid WHERE roomUser.roomToken = :roomToken")
    Mono<Set<UserEntity>> findUsersInRoom(String roomToken);
}
