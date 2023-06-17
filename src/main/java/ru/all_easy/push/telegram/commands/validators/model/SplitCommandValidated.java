package ru.all_easy.push.telegram.commands.validators.model;

import java.math.BigDecimal;
import java.util.List;

public class SplitCommandValidated {
    private Long chatId;
    private String fromUsername;
    private List<String> toUsernames;
    private BigDecimal amount;
    private int percent;
    private String title;
}
