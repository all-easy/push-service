package ru.all_easy.push.user.repository;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.room_user.repository.RoomUserEntity;

public class UserEntity implements Serializable {

    private Long id;
    private String uid;
    private String username;
    private String password;
    private Set<ExpenseEntity> myExpenses = new HashSet<>();
    private Set<RoomUserEntity> rooms = new HashSet<>();

    @Id
    public Long getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<ExpenseEntity> getMyExpenses() {
        return myExpenses;
    }

    public Set<RoomUserEntity> getRooms() {
        return rooms;
    }

    public UserEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public UserEntity setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public UserEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserEntity setMyExpenses(Set<ExpenseEntity> expenseEntity) {
        this.myExpenses = expenseEntity;
        return this;
    }

    public UserEntity setRooms(Set<RoomUserEntity> roomUsers) {
        this.rooms = roomUsers;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return id.equals(that.id) && uid.equals(that.uid) && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid, username);
    }
}
