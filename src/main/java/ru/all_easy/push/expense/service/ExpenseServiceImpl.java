package ru.all_easy.push.expense.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.repository.ExpenseRepository;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.expense.service.model.ExpenseInfoDateTime;
import ru.all_easy.push.helper.DateTimeHelper;
import ru.all_easy.push.optimize.OptimizeTools;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.service.UserService;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final OptimizeTools optimizeTools;
    private final UserService userService;
    private final DateTimeHelper dateTimeHelper;
    private final ExpenseRepository repository;

    public ExpenseServiceImpl(
            OptimizeTools optimizeTools,
            UserService userService,
            DateTimeHelper dateTimeHelper,
            ExpenseRepository repository) {
        this.optimizeTools = optimizeTools;
        this.userService = userService;
        this.dateTimeHelper = dateTimeHelper;
        this.repository = repository;
    }

    @Override
    public Map<String, BigDecimal> optimize(RoomEntity room) {
        List<ExpenseEntity> roomExpenses = findRoomExpensesByCurrency(room);
        return optimizeTools.optimize(roomExpenses);
    }

    @Transactional
    @Override
    public ExpenseEntity expense(ExpenseInfo expenseInfo, RoomEntity room) {
        UserEntity from = userService.findUserByUid(expenseInfo.fromUid());
        UserEntity to = userService.findUserByUid(expenseInfo.toUid());

        ExpenseEntity expense = new ExpenseEntity()
                .setRoom(room)
                .setTo(to)
                .setFrom(from)
                .setName(expenseInfo.name())
                .setDateTime(dateTimeHelper.now())
                .setAmount(expenseInfo.amount())
                .setCurrency(room.getCurrency());

        return repository.save(expense);
    }

    public List<ExpenseInfoDateTime> findLimitRoomExpenses(String roomToken, Integer limit) {
        Page<ExpenseEntity> page = repository.findAllByRoomToken(
                roomToken, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "dateTime")));

        return page.get()
                .map(p -> new ExpenseInfoDateTime(
                        p.getFrom().getUsername(),
                        p.getTo().getUsername(),
                        p.getAmount(),
                        p.getName(),
                        p.getDateTime(),
                        p.getCurrency().getCode() + " " + p.getCurrency().getSymbol()))
                .sorted(Comparator.comparing(ExpenseInfoDateTime::dateTime))
                .collect(Collectors.toList());
    }

    public List<ExpenseEntity> findRoomExpenses(RoomEntity room) {
        return repository.findAllByRoom(room);
    }

    public List<ExpenseEntity> findRoomExpensesByCurrency(RoomEntity room) {
        return repository.findAllByRoomAndCurrency(room, room.getCurrency());
    }
}
