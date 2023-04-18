package ru.all_easy.push.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    @Query("SELECT user FROM UserEntity user LEFT JOIN user.rooms WHERE user.uid = :uid")
    UserEntity findUserEntity(String uid);

    @Query("SELECT user FROM UserEntity user JOIN RoomUserEntity roomUser ON user.uid = roomUser.userUid WHERE roomUser.roomToken = :roomToken")
    Set<UserEntity> findUsersInRoom(String roomToken);
}
