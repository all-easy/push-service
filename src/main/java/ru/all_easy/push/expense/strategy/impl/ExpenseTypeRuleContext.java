package ru.all_easy.push.expense.strategy.impl;

import org.springframework.stereotype.Component;

import ru.all_easy.push.expense.controller.ExpenseType;
import ru.all_easy.push.expense.strategy.ExpenseTypeRule;
import ru.all_easy.push.expense.strategy.model.ExpenseRuleInfo;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExpenseTypeRuleContext {

    private final Map<ExpenseType, ExpenseTypeRule> ruleMap;

    public ExpenseTypeRuleContext(AllRule allRule,
                                  IndividualRule individualRule) {
        this.ruleMap = new HashMap<>();
        this.ruleMap.put(ExpenseType.ALL, allRule);
        this.ruleMap.put(ExpenseType.INDIVIDUAL, individualRule);
    }

    public ExpenseResult process(ExpenseRuleInfo expenseRuleInfo) {
        if (ruleMap.containsKey(expenseRuleInfo.type())) {
            return ruleMap.get(expenseRuleInfo.type()).process(expenseRuleInfo);
        }

        return null;
    }
}
