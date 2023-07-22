package ru.all_easy.push.room.repository.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.room_user.repository.RoomUserEntity;

public class RoomEntity implements Serializable {

    @Id
    private Long id;

    private String title;
    private String token;
    private Set<RoomUserEntity> users = new HashSet<>();
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

    public Set<RoomUserEntity> getUsers() {
        return users;
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

    public RoomEntity setUsers(Set<RoomUserEntity> roomUser) {
        this.users = roomUser;
        return this;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomEntity that = (RoomEntity) o;
        return id.equals(that.id) && title.equals(that.title) && token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, token);
    }
}
