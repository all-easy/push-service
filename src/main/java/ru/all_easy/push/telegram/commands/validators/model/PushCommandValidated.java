package ru.all_easy.push.telegram.commands.validators.model;

import java.math.BigDecimal;

public class PushCommandValidated {
    private Long chatId;
    private String fromUsername;
    private String toUsername;
    private BigDecimal amount;
    private String name;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public PushCommandValidated setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
        return this;
    }

    public String getToUsername() {
        return toUsername;
    }

    public PushCommandValidated setToUsername(String toUsername) {
        this.toUsername = toUsername;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PushCommandValidated setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getName() {
        return name;
    }

    public PushCommandValidated setName(String name) {
        this.name = name;
        return this;
    }
}
