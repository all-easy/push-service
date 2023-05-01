package ru.all_easy.push.expense.service;

import org.springframework.stereotype.Service;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.repository.ExpenseRepository;
import ru.all_easy.push.optimize.OptimizeTools;
import ru.all_easy.push.room.repository.model.RoomEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseHelperImpl implements ExpenseHelper {
    private final ExpenseRepository repository;
    private final OptimizeTools optimizeTools;

    public ExpenseHelperImpl(ExpenseRepository repository, OptimizeTools optimizeTools) {
        this.repository = repository;
        this.optimizeTools = optimizeTools;
    }

    @Override
    public Map<String, BigDecimal> optimize(RoomEntity room, Long chatId) {
        List<ExpenseEntity> roomExpenses = findRoomExpenses(room);
        return optimizeTools.optimize(roomExpenses);
    }

    public List<ExpenseEntity> findRoomExpenses(RoomEntity room) {
        return repository.findAllByRoom(room);
    }
}
