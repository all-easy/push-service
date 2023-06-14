package ru.all_easy.push.room.repository.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.NaturalId;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.user.repository.UserEntity;

@Entity
@Table(name = "room")
public class RoomEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @NaturalId
    @Column(name = "token")
    private String token;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoomUserEntity> users = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", referencedColumnName = "code")
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

    public RoomUserEntity addRoomUser(RoomUserEntity roomUser) {
        users.add(roomUser);
        roomUser.getUser().getRooms().add(roomUser);

        return roomUser;
    }

    public void removeUser(UserEntity user) {
        for (Iterator<RoomUserEntity> iterator = users.iterator(); iterator.hasNext(); ) {
            RoomUserEntity roomUser = iterator.next();

            if (roomUser.getRoom().equals(this) && roomUser.getUser().equals(user)) {
                iterator.remove();
                roomUser.getUser().getRooms().remove(roomUser);
                roomUser.setRoom(null);
                roomUser.setUser(null);
            }
        }
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
