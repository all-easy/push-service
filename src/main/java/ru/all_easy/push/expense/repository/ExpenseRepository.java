package ru.all_easy.push.expense.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.room.repository.model.RoomEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findAllByRoom(RoomEntity roomEntity);

    List<ExpenseEntity> findAllByRoomAndCurrency(RoomEntity roomEntity, CurrencyEntity currency);

    Page<ExpenseEntity> findAllByRoomToken(String roomToken, PageRequest pageRequest);
}
