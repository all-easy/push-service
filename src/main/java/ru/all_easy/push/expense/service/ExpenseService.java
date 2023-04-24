package ru.all_easy.push.expense.service;

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

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final UserService userService;
    private final DateTimeHelper dateTimeHelper;
    private final ExpenseRepository repository;
    private final OptimizeTools optimizeTools;

    public ExpenseService(UserService userService,
                          DateTimeHelper dateTimeHelper,
                          ExpenseRepository repository,
                          OptimizeTools optimizeTools) {
        this.userService = userService;
        this.dateTimeHelper = dateTimeHelper;
        this.repository = repository;
        this.optimizeTools = optimizeTools;
    }

    @Transactional
    public ExpenseEntity expense(ExpenseInfo expenseInfo, RoomEntity room) {
        UserEntity from = userService.findUserByUid(expenseInfo.fromUid());
        UserEntity to = userService.findUserByUid(expenseInfo.toUid());

        ExpenseEntity expense = new ExpenseEntity()
                .setRoom(room)
                .setTo(to)
                .setFrom(from)
                .setName(expenseInfo.name())
                .setDateTime(dateTimeHelper.now())
                .setAmount(expenseInfo.amount());

        return repository.save(expense);
    }

    public Map<String, BigDecimal> optimize(RoomEntity room) {
        List<ExpenseEntity> roomExpenses = findRoomExpenses(room);
        return optimizeTools.optimize(roomExpenses);
    }

    public List<ExpenseInfoDateTime> findLimitRoomExpenses(String roomToken, Integer limit) {
        Page<ExpenseEntity> page = repository.findAllByRoomToken(roomToken,
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "dateTime")));
        
        
        return page.get()
                .map(p -> new ExpenseInfoDateTime(
                        p.getFrom().getUsername(),
                        p.getTo().getUsername(),
                        p.getAmount(),
                        p.getName(),
                        p.getDateTime()))
                .sorted(Comparator.comparing(ExpenseInfoDateTime::dateTime))
                .collect(Collectors.toList());
    }

    public List<ExpenseEntity> findRoomExpenses(RoomEntity room) {
        return repository.findAllByRoom(room);
    }
}
