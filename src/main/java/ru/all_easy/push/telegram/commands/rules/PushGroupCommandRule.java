package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
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
        if (update.message() == null || update.message().text() == null) {
            return false;
        }
        return update.message().text().contains(Commands.PUSH.getCommand())
                && (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        var validated = pushCommandValidator.validate(update);
        if (validated.hasError()) {
            return ResultK.Err(new CommandError(validated.getError().message(), chatId));
        }

        var result = pushGroupCommandService.push(validated.getResult());
        if (result.hasError()) {
            return ResultK.Err(new CommandError(validated.getError().message(), chatId));
        }

        RoomUserEntity fromEntity = findRoomUser(roomEntity, validated.getResult().getFromUsername());
        if (fromEntity == null) {
            return ResultK.Err(new CommandError(AnswerMessageTemplate.UNADDED_USER.getMessage(), chatId));
        }

        RoomUserEntity toEntity = findRoomUser(roomEntity, validated.getResult().getToUsername());
        if (toEntity == null) {
            return ResultK.Err(new CommandError(
                    String.format(
                        AnswerMessageTemplate.UNADDED_USER.getMessage(),
                        validated.getResult().getToUsername()), chatId));
        }

        ExpenseInfo info = new ExpenseInfo(
                roomEntity.getToken(),
                validated.getResult().getAmount().compareTo(BigDecimal.ZERO) < 0
                        ? toEntity.getUserUid()
                        : fromEntity.getUserUid(),
                validated.getResult().getAmount().compareTo(BigDecimal.ZERO) < 0
                        ? fromEntity.getUserUid()
                        : toEntity.getUserUid(),
                validated.getResult().getAmount().abs(),
                validated.getResult().getName());

        ExpenseEntity result = expenseService.expense(info, roomEntity);
        String answerMessage = String.format(
            "Expense *%.2f*%s to user *%s* has been successfully added, description: %s",
                result.getAmount(),
                roomEntity.getCurrency() == null ? "" :
                        " " + roomEntity.getCurrency().getSymbol() + " " + roomEntity.getCurrency().getCode(),
            result.getTo().getUsername(),

            result.getName());

        return ResultK.Ok(new CommandProcessed(answerMessage, chatId));
    }

        return ResultK.Ok(new CommandProcessed(result.getResult()));
    }
    
}
