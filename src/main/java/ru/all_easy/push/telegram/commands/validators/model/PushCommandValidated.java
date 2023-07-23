package ru.all_easy.push.telegram.commands.validators.model;

import java.math.BigDecimal;
import ru.all_easy.push.common.Result;
import ru.all_easy.push.common.client.model.ReplayMarkup;

public class PushCommandValidated implements Result {
    private String fromUsername;
    private String toUsername;
    private BigDecimal amount;
    private String name;
    private Long chatId;
    private Integer replayToId;
    private String message;
    private ReplayMarkup replayMarkup;

    public String fromUsername() {
        return fromUsername;
    }

    public PushCommandValidated setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
        return this;
    }

    public String toUsername() {
        return toUsername;
    }

    public PushCommandValidated setToUsername(String toUsername) {
        this.toUsername = toUsername;
        return this;
    }

    public BigDecimal amount() {
        return amount;
    }

    public PushCommandValidated setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String name() {
        return name;
    }

    public PushCommandValidated setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Long chatId() {
        return chatId;
    }

    public PushCommandValidated setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    @Override
    public Integer replayToId() {
        return replayToId;
    }

    public PushCommandValidated setReplayToId(Integer replayToId) {
        this.replayToId = replayToId;
        return this;
    }

    @Override
    public String message() {
        return message;
    }

    public PushCommandValidated setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public ReplayMarkup replayMarkup() {
        return replayMarkup;
    }

    public PushCommandValidated setReplayMarkup(ReplayMarkup replayMarkup) {
        this.replayMarkup = replayMarkup;
        return this;
    }
}
