package ru.all_easy.push.expense.strategy.impl;

import org.springframework.stereotype.Component;

import ru.all_easy.push.expense.service.ExpenseServiceImpl;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.expense.strategy.ExpenseTypeRule;
import ru.all_easy.push.expense.strategy.model.ExpenseRuleInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;

import java.math.BigDecimal;

@Component
public class IndividualRule implements ExpenseTypeRule {

    private final RoomService roomService;
    private final ExpenseServiceImpl expenseService;
    private final MathHelper mathHelper;

    public IndividualRule(RoomService roomService,
                          ExpenseServiceImpl expenseService,
                          MathHelper mathHelper) {
        this.roomService = roomService;
        this.expenseService = expenseService;
        this.mathHelper = mathHelper;
    }

    @Override
    public ExpenseResult process(ExpenseRuleInfo expenseRuleInfo) {
        RoomEntity room = roomService.findRoomByToken(expenseRuleInfo.roomToken());

        BigDecimal amount = mathHelper.calculate(expenseRuleInfo.amountStr());
        ExpenseInfo expenseInfo = new ExpenseInfo(
                expenseRuleInfo.roomToken(),
                expenseRuleInfo.fromUid(),
                expenseRuleInfo.toUid(),
                amount,
                expenseRuleInfo.name()
        );
        ExpenseEntity savedExpense = expenseService.expense(expenseInfo, room);

        return new ExpenseResult(
                savedExpense.getFrom().getUsername(),
                savedExpense.getTo().getUsername(),
                savedExpense.getAmount(),
                expenseRuleInfo.type());
    }
}
