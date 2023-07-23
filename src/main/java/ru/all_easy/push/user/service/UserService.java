package ru.all_easy.push.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.repository.UserRepository;
import ru.all_easy.push.user.service.model.RegisterInfo;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final DatabaseClient db;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, DatabaseClient db) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.db = db;
    }

    public Mono<UserEntity> findUserByUid(String uid) {
        return findUserEntity(uid);
    }

    public Mono<UserEntity> registerEntity(RegisterInfo info) {
        return findUserEntity(info.id()).switchIfEmpty(Mono.defer(() -> createUser(info)));
    }

    public Mono<UserEntity> findUserEntity(String uid) {
        return repository.findUserEntity(uid);
    }

    public Mono<UserEntity> findUserInRoomByUsername(String roomToken, String username) {
        return repository.findUserInRoomByUsername(roomToken, username);
    }

    private Mono<UserEntity> createUser(RegisterInfo info) {
        return repository.save(new UserEntity()
                .setUsername(info.username())
                .setPassword(passwordEncoder.encode(info.password()))
                .setUid(info.id()));
    }
}
