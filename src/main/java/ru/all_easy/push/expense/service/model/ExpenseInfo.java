package ru.all_easy.push.expense.service.model;

import java.math.BigDecimal;

public record ExpenseInfo(String roomToken, String fromUid, String toUid, BigDecimal amount, String name) {}
