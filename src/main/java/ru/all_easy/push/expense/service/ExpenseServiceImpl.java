package ru.all_easy.push.expense.service;

import org.springframework.stereotype.Service;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.optimize.OptimizeTools;
import ru.all_easy.push.room.repository.model.RoomEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final OptimizeTools optimizeTools;
    private final ExpenseServiceHelper expenseServiceHelper;

    public ExpenseServiceImpl(OptimizeTools optimizeTools,
                              ExpenseServiceHelper expenseServiceHelper) {
        this.optimizeTools = optimizeTools;
        this.expenseServiceHelper = expenseServiceHelper;
    }

    @Override
    public Map<String, BigDecimal> optimize(RoomEntity room) {
        List<ExpenseEntity> roomExpenses = expenseServiceHelper.findRoomExpenses(room);
        return optimizeTools.optimize(roomExpenses);
    }

}
