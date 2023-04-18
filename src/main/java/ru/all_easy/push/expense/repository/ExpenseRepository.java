package ru.all_easy.push.expense.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.all_easy.push.room.repository.model.RoomEntity;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findAllByRoom(RoomEntity roomEntity);

    Page<ExpenseEntity> findAllByRoomToken(String roomToken, PageRequest pageRequest);

}
