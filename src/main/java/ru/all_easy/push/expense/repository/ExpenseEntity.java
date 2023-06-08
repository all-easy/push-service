package ru.all_easy.push.expense.repository;

import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.user.repository.UserEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name="from_uid", nullable=false, referencedColumnName = "uid")
    public UserEntity getFrom() {
        return from;
    }

    @ManyToOne
    @JoinColumn(name="to_uid", nullable=false, referencedColumnName = "uid")
    public UserEntity getTo() {
        return to;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_token", nullable=false, referencedColumnName = "token")
    public RoomEntity getRoom() {
        return room;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @Column(name = "datetime")
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", referencedColumnName = "code")
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
