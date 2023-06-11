package ru.all_easy.push.expense.service;

import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;

import java.math.BigDecimal;
import java.util.Map;

public interface ExpenseService {
    Map<String, BigDecimal> optimize(RoomEntity room);

    ExpenseEntity expense(ExpenseInfo expenseInfo, RoomEntity room);
}
