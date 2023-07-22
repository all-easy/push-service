package ru.all_easy.push.room.repository;

import java.util.List;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;
import ru.all_easy.push.room.repository.model.RoomEntity;

public interface RoomRepository extends R2dbcRepository<RoomEntity, Long> {

    @Query("SELECT DISTINCT room FROM RoomEntity room " + "LEFT JOIN FETCH room.users ru "
            + "LEFT JOIN FETCH ru.user "
            + "WHERE room.token = :roomToken")
    Mono<RoomEntity> findByToken(String roomToken);

    @Query("SELECT DISTINCT room FROM RoomEntity room " + "LEFT JOIN FETCH room.users u " + "WHERE u.user.uid = :uid")
    Mono<List<RoomEntity>> findAllByUid(String uid);

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
    Mono<Integer> deleteRoomByRoomToken(@Param("migrateFromChatId") String migrateFromChatId);
}
