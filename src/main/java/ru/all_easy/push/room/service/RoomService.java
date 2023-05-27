package ru.all_easy.push.room.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.all_easy.push.common.ErrorType;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.optimize.OptimizeTools;
import ru.all_easy.push.optimize.OweInfo;
import ru.all_easy.push.room.repository.RoomRepository;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.repository.model.RoomStatus;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.room.service.exception.RoomServiceException;
import ru.all_easy.push.room.service.model.RoomInfo;
import ru.all_easy.push.room.service.model.RoomJoinInfo;
import ru.all_easy.push.room.service.model.RoomResult;
import ru.all_easy.push.room.service.model.RoomUserInfo;
import ru.all_easy.push.room.service.model.UserRoomResult;
import ru.all_easy.push.room_user.service.RoomUserService;
import ru.all_easy.push.shape.repository.ShapeEntity;
import ru.all_easy.push.shape.service.ShapeService;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.service.UserService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository repository;

    private final ShapeService shapeService;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final RoomUserService roomUserService;

    private final OptimizeTools optimizeTools;

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    public RoomService(RoomRepository repository,
                       ShapeService shapeService,
                       UserService userService,
                       ExpenseService expenseService,
                       RoomUserService roomUserService,
                       OptimizeTools optimizeTools) {
        this.repository = repository;
        this.shapeService = shapeService;
        this.userService = userService;
        this.expenseService = expenseService;
        this.roomUserService = roomUserService;
        this.optimizeTools = optimizeTools;
    }

    public RoomResult createRoom(RoomInfo roomInfo) {
        RoomEntity savedRoom = createRoomEntity(roomInfo);

        Set<UserEntity> users = savedRoom.getUsers().stream()
                .map(RoomUserEntity::getUser)
                .collect(Collectors.toSet());
        return createRoomResult(savedRoom, Collections.emptySet(), users, null);
    }
    
    public RoomEntity createRoomEntity(RoomInfo roomInfo) {
        RoomEntity savedRoom = findByToken(roomInfo.token());
        if (savedRoom != null) {
            return savedRoom;
        }

        RoomEntity room = new RoomEntity()
                .setTitle(roomInfo.title())
                .setToken(roomInfo.token());

        return repository.save(room);
    }

    @Transactional
    public RoomResult enterRoom(UserEntity user, RoomEntity room) {
        RoomUserEntity roomUser = room.getUsers().stream()
            .filter(rm -> rm.getUser().getUid().equals(user.getUid()))
            .findFirst()
            .orElse(null);
        if (roomUser == null) {
            roomUser = new RoomUserEntity(room, user);
        }

        RoomUserEntity roomUserEntity = room.addRoomUser(roomUser);
        roomUserEntity.setStatus(RoomStatus.ACTIVE);

        return new RoomResult(null, room.getToken(), room.getTitle(), null, null);
    }

    @Transactional
    public RoomResult join(RoomJoinInfo roomJoinInfo) {
        RoomEntity savedRoom = findRoomByToken(roomJoinInfo.roomToken());

        Set<ShapeEntity> roomShapes = shapeService.findAllShapesInRoom(roomJoinInfo.roomToken());

        UserEntity user = userService.findUserByUid(roomJoinInfo.uid());

        Set<UserEntity> usersInRoom = userService.findUsersInRoom(savedRoom.getToken());

        RoomUserEntity roomUser = roomUserService.findByUserAndRoom(user, savedRoom);
        if (roomUser == null) {
            roomUser = new RoomUserEntity(savedRoom, user);
        }

        List<RoomUserEntity> roomUsers = roomUserService.findByUser(user);
        roomUsers.forEach(sr -> sr.setStatus(RoomStatus.NOT_ACTIVE));

        RoomUserEntity roomUserEntity = savedRoom.addRoomUser(roomUser);
        roomUserEntity.setStatus(RoomStatus.ACTIVE);

        if (usersInRoom.contains(user)) {
            Set<OweInfo> optimized = optimize(roomJoinInfo.username(), roomJoinInfo.roomToken());
            return createRoomResult(savedRoom, roomShapes, usersInRoom, optimized);
        }

        ShapeEntity userShape = roomShapes.stream()
                .filter(s -> s.getShape().equals(roomJoinInfo.shape()))
                .findFirst()
                .orElse(null);
        if (userShape != null) {
            throw new RoomServiceException()
                    .setMessage("Selected shape already exist in this room")
                    .setErrorType(ErrorType.shape_exist)
                    .setCode(302);
        }

        ShapeEntity shapeEntity = new ShapeEntity()
                .setShape(roomJoinInfo.shape())
                .setRoomToken(roomJoinInfo.roomToken())
                .setUserUid(roomJoinInfo.uid());
        ShapeEntity savedShape = shapeService.save(shapeEntity);

        RoomResult roomResult = createRoomResult(savedRoom, roomShapes, usersInRoom, Collections.emptySet());
        roomResult.users()
                .add(new RoomUserInfo(user.getUsername(), user.getUid(), savedShape.getShape()));

        return roomResult;
    }

    @Transactional
    public RoomResult roomInfo(RoomJoinInfo roomJoinInfo) {
        RoomEntity savedRoom = findRoomByToken(roomJoinInfo.roomToken());
        Set<UserEntity> usersInRoom = userService.findUsersInRoom(savedRoom.getToken());

        UserEntity savedUser = userService.findUserByUid(roomJoinInfo.uid());
        if (!usersInRoom.contains(savedUser)) {
            logger.info("You are not member of this room");
            throw new RoomServiceException()
                    .setMessage("You are not member of this room")
                    .setErrorType(ErrorType.not_room_member)
                    .setCode(400);
        }

        Set<ShapeEntity> roomShapes = shapeService.findAllShapesInRoom(roomJoinInfo.roomToken());

        Set<OweInfo> optimized = optimize(roomJoinInfo.username(), roomJoinInfo.roomToken());

        usersInRoom = usersInRoom.stream()
                .filter(user -> !user.getUsername().equals(roomJoinInfo.username()))
                .collect(Collectors.toSet());
        return createRoomResult(savedRoom, roomShapes, usersInRoom, optimized);
    }

    private Set<OweInfo> optimize(String username, String roomToken) {
        RoomEntity room = repository.findByToken(roomToken);
        Map<String, BigDecimal> optimized = expenseService.optimize(room);
        return optimizeTools.getOwes(username, optimized);
    }

    private RoomResult createRoomResult(RoomEntity room,
                                        Set<ShapeEntity> shapes,
                                        Set<UserEntity> usersInRoom,
                                        Set<OweInfo> owes) {
        Set<RoomUserInfo> roomUsers = usersInRoom.stream()
                .map(u -> {
                    ShapeEntity shape = shapes.stream()
                            .filter(s -> s.getUserUid().equals(u.getUid()))
                            .findFirst()
                            .orElse(null);
                    return new RoomUserInfo(
                            u.getUsername(),
                            u.getUid(),
                            shape != null ? shape.getShape() : null);
                })
                .collect(Collectors.toSet());
        return new RoomResult(null, room.getToken(), room.getTitle(), roomUsers, owes);
    }

    public RoomEntity findRoomByToken(String token) {
        RoomEntity savedRoom = findByToken(token);
        if (savedRoom == null) {            
            throw new RoomServiceException()
                    .setMessage("Room with token: " + token + " not found")
                    .setCode(404);
        }
        return savedRoom;
    }

    public RoomEntity findByToken(String token) {
        return repository.findByToken(token);
    }

    public Set<UserRoomResult> getUserRooms(String uid) {
        List<RoomEntity> userRooms = repository.findAllByUid(uid);
        return userRooms.stream()
                .map(ur -> {
                    RoomUserEntity userRoom = findUserRoom(uid, ur.getUsers());
                    return new UserRoomResult(ur.getToken(), ur.getTitle(), userRoom.getStatus());
                }).collect(Collectors.toSet());
    }

    private RoomUserEntity findUserRoom(String uid, Set<RoomUserEntity> roomUsers) {
        return roomUsers.stream()
                .filter(u -> u.getUserUid().equals(uid))
                .findFirst().orElseThrow(() -> {
                    logger.info("Cannot find user from room roomUser with uid: {}", uid);
                    throw new RoomServiceException().setMessage("Something went wrong");
                });
    }

    @Transactional
    public Boolean exitRoom(String uid, String roomToken) {
        RoomEntity room = findRoomByToken(roomToken);
        UserEntity user = userService.findUserByUid(uid);
        RoomUserEntity roomUser = roomUserService.findByUserAndRoom(user, room);

        roomUser.setStatus(RoomStatus.NOT_ACTIVE);

        return true;
    }

    public void setRoomCurrency(RoomEntity room, CurrencyEntity currency) {
        room.setCurrency(currency);
        repository.save(room);
    }

}
