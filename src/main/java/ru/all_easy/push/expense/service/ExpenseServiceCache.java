package ru.all_easy.push.expense.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.all_easy.push.room.repository.model.RoomEntity;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Primary
public class ExpenseServiceCache implements ExpenseService {
    private final ExpenseServiceImpl expenseService;

    public ExpenseServiceCache(ExpenseServiceImpl expenseService) {
        this.expenseService = expenseService;
    }

    @Override
    @Cacheable(value = "results", key = "#room.getToken()")
    public Map<String, BigDecimal> optimize(RoomEntity room) {
        return expenseService.optimize(room);
    }
}
