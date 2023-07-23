package ru.all_easy.push.expense.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("expense")
public class ExpenseEntity {

    @Id
    private Long id;

    @Column("from_uid")
    private String fromUid;

    @Column("to_uid")
    private String toUid;

    @Column("room_token")
    private String roomToken;

    private String name;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private String currency;

    @Id
    public Long getId() {
        return id;
    }

    public String getFromUid() {
        return fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public String getRoomToken() {
        return roomToken;
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

    public String getCurrency() {
        return currency;
    }

    public ExpenseEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public ExpenseEntity setFromUid(String fromUid) {
        this.fromUid = fromUid;
        return this;
    }

    public ExpenseEntity setToUid(String toUid) {
        this.toUid = toUid;
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

    public ExpenseEntity setRoomToken(String roomToken) {
        this.roomToken = roomToken;
        return this;
    }

    public ExpenseEntity setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public ExpenseEntity setCurrency(String currency) {
        this.currency = currency;
        return this;
    }
}
