package ru.all_easy.push.expense.service;

import java.math.BigDecimal;
import java.util.Map;
import reactor.core.publisher.Mono;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;

public interface ExpenseService {
    Mono<Map<String, BigDecimal>> optimize(RoomEntity room);

    Mono<ExpenseEntity> expense(ExpenseInfo expenseInfo, RoomEntity room);
}
