package ru.all_easy.push.expense.strategy.impl;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import org.springframework.stereotype.Component;

import ru.all_easy.push.expense.controller.ExpenseType;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.expense.strategy.ExpenseTypeRule;
import ru.all_easy.push.expense.strategy.model.ExpenseRuleInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.service.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Component
public class AllRule implements ExpenseTypeRule {

    private final RoomService roomService;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final DoubleEvaluator doubleEvaluator;

    public AllRule(RoomService roomService,
                   UserService userService,
                   ExpenseService expenseService,
                   DoubleEvaluator doubleEvaluator) {
        this.roomService = roomService;
        this.userService = userService;
        this.expenseService = expenseService;
        this.doubleEvaluator = doubleEvaluator;
    }

    @Override
    public ExpenseResult process(ExpenseRuleInfo expenseRuleInfo) {
        RoomEntity room = roomService.findRoomByToken(expenseRuleInfo.roomToken());
        Set<UserEntity> usersInRoom = userService.findUsersInRoom(expenseRuleInfo.roomToken());

        int numberOfUsers = usersInRoom.size();
        BigDecimal amount = calculate(expenseRuleInfo.amountStr());
        BigDecimal amountPerUser = amount.divide(BigDecimal.valueOf(numberOfUsers), 4, RoundingMode.FLOOR);

        usersInRoom.stream()
                .filter(user -> !user.getUid().equals(expenseRuleInfo.fromUid()))
                .forEach(user -> {
                    ExpenseInfo expenseInfo = new ExpenseInfo(
                            expenseRuleInfo.roomToken(),
                            expenseRuleInfo.fromUid(),
                            user.getUid(),
                            amountPerUser,
                            expenseRuleInfo.name()
                    );
                    expenseService.expense(expenseInfo, room);
                });

        return new ExpenseResult(
                "",
                "",
                amountPerUser,
                ExpenseType.ALL
        );
    }

    private BigDecimal calculate(String amountStr) {
        Double evaluated = doubleEvaluator.evaluate(amountStr);
        return BigDecimal.valueOf(evaluated);
    }
}
