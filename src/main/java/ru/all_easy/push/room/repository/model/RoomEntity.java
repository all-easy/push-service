package ru.all_easy.push.room.repository.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;

@Table("room")
public class RoomEntity implements Serializable {

    @Id
    private Long id;

    private String title;
    private String token;

    private CurrencyEntity currency;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getToken() {
        return token;
    }

    public CurrencyEntity getCurrency() {
        return currency;
    }

    public RoomEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public RoomEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public RoomEntity setToken(String token) {
        this.token = token;
        return this;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }
}
