package ru.all_easy.push.telegram.commands.rules;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room.service.exception.RoomServiceException;
import ru.all_easy.push.room.service.model.RoomInfo;
import ru.all_easy.push.room.service.model.RoomResult;
import ru.all_easy.push.shape.repository.Shape;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.service.UserService;
import ru.all_easy.push.user.service.model.RegisterInfo;

@Service
public class AddCommandRule implements CommandRule {

    private final RoomService roomService;
    private final UserService userService;
    private  final TelegramService telegramService;

    public AddCommandRule(RoomService roomService,
                          UserService userService,
                          TelegramService telegramService) {
        this.roomService = roomService;
        this.userService = userService;
        this.telegramService = telegramService;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.ADD_ME.getCommand());
    }

    @Override
    @Transactional
    public void process(Update update) {
        String userId = String.valueOf(update.message().from().id());
        String roomId = String.valueOf(update.message().chat().id());
        String username = update.message().from().username();
        Long chatId = update.message().chat().id();
        
        if (username == null) {
            sendMessage(chatId, "Add username in Telegram settings please");
            return;
        }

        try {
            String chatTitle = update.message().chat().title();
            if (chatTitle == null) {
                chatTitle = update.message().from().username();
            }
            RoomEntity room = roomService.createRoomEntity(new RoomInfo(userId, chatTitle, roomId, Shape.EMPTY));
            UserEntity user = userService.registerEntity(new RegisterInfo(username, StringUtils.EMPTY, userId));
            RoomResult result = roomService.enterRoom(user, room);
            String message = String.format("*%s* have been successfully added to virtual room *%s*", username, result.title());
            sendMessage(chatId, message);
        } catch (RoomServiceException ex) {
            sendMessage(chatId, ex.getMessage());
        }
    }

    private void sendMessage(Long chatId, String message) {
        telegramService.sendMessage(new SendMessageInfo(chatId, message, ParseMode.MARKDOWN.getMode()));
    }
}
