package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.service.PushGroupCommandService;
import ru.all_easy.push.telegram.commands.validators.PushCommandValidator;

@Service
public class PushGroupCommandRule implements CommandRule {
    private final PushGroupCommandService pushGroupCommandService;
    private final PushCommandValidator pushCommandValidator;

    public PushGroupCommandRule(PushGroupCommandService pushGroupCommandService,
                                PushCommandValidator pushCommandValidator) {
        this.pushGroupCommandService = pushGroupCommandService;
        this.pushCommandValidator = pushCommandValidator;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.PUSH.getCommand())
                && (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        var validated = pushCommandValidator.validate(update);
        if (validated.hasError()) {
            return ResultK.Err(new CommandError(validated.getError().message()));
        }

        var result = pushGroupCommandService.getResult(validated.getResult());
        if (result.hasError()) {
            return ResultK.Err(new CommandError(validated.getError().message()));
        }

        return ResultK.Ok(new CommandProcessed(result.getResult()));
    }

    private RoomUserEntity findRoomUser(RoomEntity room, String username) {
        return room.getUsers().stream()
            .filter(entity -> entity.getUser().getUsername().equals(username))
            .findFirst()
            .orElse(null);
    } 
    
}
