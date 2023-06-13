package ru.all_easy.push.room_user.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.user.repository.UserEntity;

public interface RoomUserRepository extends JpaRepository<RoomUserEntity, Long> {

    RoomUserEntity findByUserAndRoom(UserEntity user, RoomEntity room);

    List<RoomUserEntity> findByUser(UserEntity user);
}
