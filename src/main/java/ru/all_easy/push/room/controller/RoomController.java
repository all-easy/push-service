package ru.all_easy.push.room.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.all_easy.push.common.AbstractAuthentication;
import ru.all_easy.push.common.Result;
import ru.all_easy.push.helper.HashGenerator;
import ru.all_easy.push.room.controller.model.RoomRequest;
import ru.all_easy.push.room.controller.model.RoomResponse;
import ru.all_easy.push.room.controller.model.RoomUser;
import ru.all_easy.push.room.controller.model.UserRoom;
import ru.all_easy.push.room.controller.model.UserRoomResponse;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room.service.model.RoomInfo;
import ru.all_easy.push.room.service.model.RoomJoinInfo;
import ru.all_easy.push.room.service.model.RoomResult;
import ru.all_easy.push.room.service.model.UserRoomResult;
import ru.all_easy.push.shape.repository.Shape;
import ru.all_easy.push.web.security.model.User;

import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/room")
public class RoomController extends AbstractAuthentication {

    private final RoomService roomService;
    private final HashGenerator hashGenerator;

    public RoomController(RoomService roomService,
                          HashGenerator hashGenerator) {
        this.roomService = roomService;
        this.hashGenerator = hashGenerator;
    }

    @PostMapping("/")
    public Result<RoomResponse> createRoom(@RequestBody RoomRequest request) throws NoSuchAlgorithmException {
        String uid = getAuthentication().uid();
        RoomInfo roomInfo = new RoomInfo(uid, request.title(), hashGenerator.generate(), request.shape());
        RoomResult result = roomService.createRoom(roomInfo);

        return response(result);
    }

    @PostMapping("/join")
    public Result<RoomResponse> addUser(@RequestHeader("RoomToken") String roomToken,
                                        @RequestParam("shape") Shape shape) {
        User authentication = getAuthentication();
        RoomResult result = roomService.join(new RoomJoinInfo(authentication.uid(), authentication.username(), shape, roomToken));
        return response(result);
    }

    @GetMapping("/")
    public Result<RoomResponse> roomResult(@RequestHeader("RoomToken") String roomToken) {
        User authentication = getAuthentication();
        RoomResult roomResult = roomService.roomInfo(new RoomJoinInfo(authentication.uid(), authentication.username(), null, roomToken));
        return response(roomResult);
    }

    @GetMapping("/list")
    public Result<UserRoomResponse> userRooms() {
        User auth = getAuthentication();
        Set<UserRoomResult> userRoomsResult = roomService.getUserRooms(auth.uid());

        Set<UserRoom> userRooms = userRoomsResult.stream()
                .map(ur -> new UserRoom(ur.token(), ur.name(), ur.status()))
                .collect(Collectors.toSet());

        return new Result<>(new UserRoomResponse(userRooms));
    }

    @PostMapping("/exit")
    public Result<Boolean> exitRoom(@RequestHeader("RoomToken") String roomToken) {
        User auth = getAuthentication();
        Boolean result = roomService.exitRoom(auth.uid(), roomToken);

        return new Result<>(result);
    }

    private Result<RoomResponse> response(RoomResult result) {
        Set<RoomUser> roomUsers = result.users().stream()
                .map(u -> new RoomUser(u.username(), u.uid(), u.shape()))
                .collect(Collectors.toSet());
        RoomResponse response = new RoomResponse(result.token(), result.title(), roomUsers, result.oweInfos());
        return new Result<>(response);
    }
}
