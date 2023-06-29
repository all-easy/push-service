package ru.all_easy.push.room.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.all_easy.push.room.repository.model.RoomEntity;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @Query("SELECT DISTINCT room FROM RoomEntity room " + "LEFT JOIN FETCH room.users ru "
            + "LEFT JOIN FETCH ru.user "
            + "WHERE room.token = :roomToken")
    RoomEntity findByToken(String roomToken);

    @Query("SELECT DISTINCT room FROM RoomEntity room " + "LEFT JOIN FETCH room.users u " + "WHERE u.user.uid = :uid")
    List<RoomEntity> findAllByUid(String uid);

    @Modifying
    @Query("UPDATE ExpenseEntity e SET e.room.token = :migrateToChatId WHERE e.room.token = :migrateFromChatId")
    void updateExpenseRoomToken(
            @Param("migrateFromChatId") String migrateFromChatId, @Param("migrateToChatId") String migrateToChatId);

    @Modifying
    @Query("UPDATE RoomUserEntity u SET u.roomToken = :migrateToChatId WHERE u.roomToken = :migrateFromChatId")
    void updateRoomUserRoomToken(
            @Param("migrateFromChatId") String migrateFromChatId, @Param("migrateToChatId") String migrateToChatId);

    @Modifying
    @Query("DELETE FROM RoomEntity r WHERE r.token = :migrateFromChatId")
    void deleteRoomByRoomToken(@Param("migrateFromChatId") String migrateFromChatId);
}
