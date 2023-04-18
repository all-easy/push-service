package ru.all_easy.push.expense.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.all_easy.push.common.AbstractAuthentication;
import ru.all_easy.push.common.Result;
import ru.all_easy.push.expense.strategy.impl.ExpenseResult;
import ru.all_easy.push.expense.strategy.impl.ExpenseTypeRuleContext;
import ru.all_easy.push.expense.strategy.model.ExpenseRuleInfo;
import ru.all_easy.push.web.security.model.User;

@RestController
@RequestMapping("/v1/api/expense")
public class ExpenseController extends AbstractAuthentication {

    private final ExpenseTypeRuleContext context;

    public ExpenseController(ExpenseTypeRuleContext context) {
        this.context = context;
    }

    @PostMapping("/")
    Result<ExpenseResponse> expense(@RequestHeader("RoomToken") String roomToken, @RequestBody ExpenseRequest request) {
        User authentication = getAuthentication();

        ExpenseRuleInfo expenseInfo = new ExpenseRuleInfo(
                roomToken,
                authentication.uid(),
                request.toUid(),
                request.amountStr(),
                request.name(),
                request.type());

        ExpenseResult expense = context.process(expenseInfo);

        ExpenseResponse response = new ExpenseResponse(
                expense.amount(),
                expense.from(),
                expense.to(),
                expense.type());

        return new Result<>(response);
    }

}