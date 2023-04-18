package ru.all_easy.push.expense.strategy.impl;

import java.math.BigDecimal;

import ru.all_easy.push.expense.controller.ExpenseType;

public record ExpenseResult(
        String from,
        String to,
        BigDecimal amount,
        ExpenseType type
) {
}
