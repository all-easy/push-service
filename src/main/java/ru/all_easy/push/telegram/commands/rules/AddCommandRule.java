package ru.all_easy.push.telegram.commands.rules;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room.service.model.RoomInfo;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.user.service.UserService;
import ru.all_easy.push.user.service.model.RegisterInfo;

@Service
public class AddCommandRule implements CommandRule {

    private final RoomService roomService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AddCommandRule.class);

    public AddCommandRule(RoomService roomService, UserService userService) {
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
    public Mono<ResultK<CommandProcessed, CommandError>> process(Update update) {
        Long chatIdL = update.message().chat().id();
        String userId = String.valueOf(update.message().from().id());
        String chatId = String.valueOf(chatIdL);
        String username = update.message().from().username();

        if (username == null) {
            return Mono.just(ResultK.Err(new CommandError(chatIdL, "Add username in Telegram settings please")));
        }

        return Mono.just(update.message().chat().title())
                .switchIfEmpty(Mono.just(update.message().from().username()))
                .flatMap(chatTitle -> roomService
                        .createRoomEntity(new RoomInfo(userId, chatTitle, chatId))
                        .flatMap(room -> userService
                                .registerEntity(new RegisterInfo(username, StringUtils.EMPTY, userId))
                                .flatMap(user -> roomService
                                        .enterRoom(user, room)
                                        .map(result -> {
                                            String message = String.format(
                                                    "*%s* have been successfully added to virtual room *%s*",
                                                    username, result.title());
                                            return ResultK.<CommandProcessed, CommandError>Ok(
                                                    new CommandProcessed(chatIdL, message));
                                        })))
                        .onErrorResume(ex -> Mono.just(ResultK.Err(new CommandError(chatIdL, ex.getMessage())))));
    }
}
