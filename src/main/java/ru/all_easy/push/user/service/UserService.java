package ru.all_easy.push.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.all_easy.push.user.exception.RegisterException;
import ru.all_easy.push.user.exception.UserServiceException;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.repository.UserRepository;
import ru.all_easy.push.user.service.model.LoginResult;
import ru.all_easy.push.user.service.model.RegisterInfo;
import ru.all_easy.push.user.service.model.RegisterResult;
import ru.all_easy.push.user.service.model.UserServiceInfo;
import ru.all_easy.push.web.security.JwtProvider;

import javax.security.auth.message.AuthException;
import java.util.Collections;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository repository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repository,
                       JwtProvider jwtProvider,
                       PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity findUserByUid(String uid) {
        UserEntity userEntity = findUserEntity(uid);
        if (userEntity == null) {
            throw new UserServiceException()
                    .setMessage("User with uid: " + uid + " not found")
                    .setErrorCode(404);
        }

        return userEntity;
    }

    public UserEntity findUserEntity(String uid) {
        return repository.findUserEntity(uid);
    }

    public RegisterResult register(RegisterInfo info) {
        if (repository.findByUsername(info.username()) != null) {
            throw new RegisterException("User with username " + info.username() + " already exist");
        }

        UserEntity savedUser = registerEntity(info);

        return new RegisterResult(savedUser.getUsername(), savedUser.getUid());
    }

    public UserEntity registerEntity(RegisterInfo info) {
        UserEntity savedUser = findUserEntity(info.id());
        if (savedUser != null) {
            return savedUser;
        }
        
        UserEntity entity = new UserEntity()
                .setUsername(info.username())
                .setPassword(passwordEncoder.encode(info.password()))
                .setUid(info.id());

        return repository.save(entity);
    }

    public LoginResult login(UserServiceInfo userServiceInfo) throws AuthException {
        UserEntity userEntity = findByLoginAndPassword(userServiceInfo.username(), userServiceInfo.password());

        String token = jwtProvider.generateToken(userEntity);

        return new LoginResult(userEntity.getUsername(), token, userEntity.getUid());
    }

    private UserEntity findByLoginAndPassword(String username, String password) throws AuthException {
        UserEntity userEntity = findUserByUsername(username);
        if (userEntity == null || !passwordEncoder.matches(password, userEntity.getPassword())) { 
            throw new UserServiceException()
                    .setMessage("Wrong username or password")
                    .setErrorCode(401);
        }
        return userEntity;
    }

    public UserEntity findUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public UserEntity save(UserEntity userByUsername) {
        return repository.save(userByUsername);
    }

    public Set<UserEntity> findUsersInRoom(String roomToken) {
        Set<UserEntity> usersInRoom = repository.findUsersInRoom(roomToken);
        if (usersInRoom == null || usersInRoom.size() == 0) {
            logger.info("Room with token: {} has no users", roomToken);
            return Collections.emptySet();
        }

        return usersInRoom;
    }
}
