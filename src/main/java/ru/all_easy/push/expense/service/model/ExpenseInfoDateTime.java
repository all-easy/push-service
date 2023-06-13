package ru.all_easy.push.expense.service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseInfoDateTime(
    String fromUsername,
    String toUsername,
    BigDecimal amount,
    String name,
    LocalDateTime dateTime,
    String currencyLabel
) {
}
