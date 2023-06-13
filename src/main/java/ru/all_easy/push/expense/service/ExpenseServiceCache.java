package ru.all_easy.push.expense.service;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;

@Service
@Primary
public class ExpenseServiceCache implements ExpenseService {
    private final ExpenseServiceImpl expenseService;

    public ExpenseServiceCache(ExpenseServiceImpl expenseService) {
        this.expenseService = expenseService;
    }

    @Override
    @Cacheable(value = "results", key = "{#room.token, #room.currency.code}")
    public Map<String, BigDecimal> optimize(RoomEntity room) {
        return expenseService.optimize(room);
    }

    @Override
    @CacheEvict(value = "results", key = "{#room.token, #room.currency.code}")
    public ExpenseEntity expense(ExpenseInfo expenseInfo, RoomEntity room) {
        return expenseService.expense(expenseInfo, room);
    }
}
