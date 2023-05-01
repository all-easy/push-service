package ru.all_easy.push.expense.service;

import ru.all_easy.push.room.repository.model.RoomEntity;

import java.math.BigDecimal;
import java.util.Map;

public interface ExpenseHelper {
    Map<String, BigDecimal> optimize(RoomEntity room, Long chatId);
}
