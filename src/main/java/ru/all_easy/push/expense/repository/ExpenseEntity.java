package ru.all_easy.push.expense.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.user.repository.UserEntity;

public class ExpenseEntity {

    private Long id;
    private UserEntity from;
    private UserEntity to;
    private RoomEntity room;
    private String name;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private CurrencyEntity currency;

    @Id
    public Long getId() {
        return id;
    }

    public UserEntity getFrom() {
        return from;
    }

    public UserEntity getTo() {
        return to;
    }

    public RoomEntity getRoom() {
        return room;
    }

    @Column("name")
    public String getName() {
        return name;
    }

    @Column("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @Column("datetime")
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public CurrencyEntity getCurrency() {
        return currency;
    }

    public ExpenseEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public ExpenseEntity setFrom(UserEntity from) {
        this.from = from;
        return this;
    }

    public ExpenseEntity setTo(UserEntity to) {
        this.to = to;
        return this;
    }

    public ExpenseEntity setName(String name) {
        this.name = name;
        return this;
    }

    public ExpenseEntity setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public ExpenseEntity setRoom(RoomEntity room) {
        this.room = room;
        return this;
    }

    public ExpenseEntity setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public ExpenseEntity setCurrency(CurrencyEntity currency) {
        this.currency = currency;
        return this;
    }
}
