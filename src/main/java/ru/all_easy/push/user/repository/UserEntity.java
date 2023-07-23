package ru.all_easy.push.user.repository;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("t_user")
public class UserEntity implements Serializable {

    private Long id;
    private String uid;
    private String username;
    private String password;

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
}
