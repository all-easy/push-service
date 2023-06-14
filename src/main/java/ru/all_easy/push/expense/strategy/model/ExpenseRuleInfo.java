package ru.all_easy.push.expense.strategy.model;

import ru.all_easy.push.expense.controller.ExpenseType;

public record ExpenseRuleInfo(
        String roomToken, String fromUid, String toUid, String amountStr, String name, ExpenseType type) {}
