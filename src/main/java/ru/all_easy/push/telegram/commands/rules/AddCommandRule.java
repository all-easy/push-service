package ru.all_easy.push.telegram.commands.rules;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room.service.exception.RoomServiceException;
import ru.all_easy.push.room.service.model.RoomInfo;
import ru.all_easy.push.room.service.model.RoomResult;
import ru.all_easy.push.shape.repository.Shape;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.service.UserService;
import ru.all_easy.push.user.service.model.RegisterInfo;

import javax.transaction.Transactional;

@Service
public class AddCommandRule implements CommandRule {

    private final RoomService roomService;
    private final UserService userService;

    public AddCommandRule(RoomService roomService,
                          UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    @Override
    public boolean apply(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }
        return update.message().text().contains(Commands.ADD_ME.getCommand());
    }

    @Override
    @Transactional
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        Long chatIdL = update.message().chat().id();
        String userId = String.valueOf(update.message().from().id());
        String chatId = String.valueOf(chatIdL);
        String username = update.message().from().username();
        
        if (username == null) {
            return ResultK.Err(
                new CommandError(chatIdL, "Add username in Telegram settings please"));
        }

        try {
            String chatTitle = update.message().chat().title();
            if (chatTitle == null) {
                chatTitle = update.message().from().username();
            }
            RoomEntity room = roomService.createRoomEntity(new RoomInfo(userId, chatTitle, chatId, Shape.EMPTY));
            UserEntity user = userService.registerEntity(new RegisterInfo(username, StringUtils.EMPTY, userId));
            RoomResult result = roomService.enterRoom(user, room);
            String message = String.format("*%s* have been successfully added to virtual room *%s*", username, result.title());
            return ResultK.Ok(new CommandProcessed(chatIdL, message));
        } catch (RoomServiceException ex) {
            return ResultK.Err(new CommandError(chatIdL, ex.getMessage()));
        }
    }
}
