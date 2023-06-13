package ru.all_easy.push.expense.strategy;

import ru.all_easy.push.expense.strategy.impl.ExpenseResult;
import ru.all_easy.push.expense.strategy.model.ExpenseRuleInfo;

public interface ExpenseTypeRule {

    ExpenseResult process(ExpenseRuleInfo expenseRuleInfo);

}
