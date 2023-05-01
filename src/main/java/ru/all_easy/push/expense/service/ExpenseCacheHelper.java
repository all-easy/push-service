package ru.all_easy.push.expense.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.all_easy.push.room.repository.model.RoomEntity;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Primary
public class ExpenseCacheHelper implements ExpenseHelper {
    private final ExpenseHelperImpl expenseHelper;

    public ExpenseCacheHelper(ExpenseHelperImpl expenseHelper) {
        this.expenseHelper = expenseHelper;
    }

    @Override
    @Cacheable(value = "results", key = "#chatId")
    public Map<String, BigDecimal> optimize(RoomEntity room, Long chatId) {
        return expenseHelper.optimize(room, chatId);
    }
}
