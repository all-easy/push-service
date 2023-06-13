package ru.all_easy.push.expense.controller;

import java.math.BigDecimal;

public record ExpenseResponse(
    BigDecimal amount,
    String fromUsername,
    String toUsername,
    ExpenseType type
) { }
