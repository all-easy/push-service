package ru.all_easy.push.expense.controller;

public record ExpenseRequest(
        String toUid,
        String amountStr,
        String name,
        ExpenseType type
) {
}


