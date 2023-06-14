package ru.all_easy.push.room.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.all_easy.push.room.repository.model.RoomEntity;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @Query("SELECT DISTINCT room FROM RoomEntity room " + "LEFT JOIN FETCH room.users ru "
            + "LEFT JOIN FETCH ru.user "
            + "WHERE room.token = :roomToken")
    RoomEntity findByToken(String roomToken);

    @Query("SELECT DISTINCT room FROM RoomEntity room " + "LEFT JOIN FETCH room.users u " + "WHERE u.user.uid = :uid")
    List<RoomEntity> findAllByUid(String uid);
}
