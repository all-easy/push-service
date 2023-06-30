package ru.all_easy.push.telegram.commands.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

@Service
public class SplitCommandService {
    private final RoomService roomService;
    private final MathHelper mathHelper;
    private final ExpenseService expenseService;

    public SplitCommandService(RoomService roomService, MathHelper mathHelper, ExpenseService expenseService) {
        this.roomService = roomService;
        this.mathHelper = mathHelper;
        this.expenseService = expenseService;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ResultK<CommandProcessed, CommandError> split(SplitCommandValidated validated) {
        RoomEntity roomEntity = roomService.findByToken(String.valueOf(validated.getChatId()));
        if (roomEntity == null) {
            return ResultK.Err(
                    new CommandError(validated.getChatId(), AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage()));
        }

        if (roomEntity.getCurrency() == null) {
            return ResultK.Err(
                    new CommandError(validated.getChatId(), AnswerMessageTemplate.UNSET_CURRENCY.getMessage()));
        }

        RoomUserEntity fromEntity = filterRoomUser(roomEntity, validated.getFromUsername());
        if (fromEntity == null) {
            return ResultK.Err(new CommandError(
                    validated.getChatId(),
                    String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.getFromUsername())));
        }

        List<RoomUserEntity> toEntities = new ArrayList<>(roomEntity.getUsers());
        BigDecimal amountForEach =
                validated.getAmount().abs().divide(BigDecimal.valueOf(toEntities.size()), 2, RoundingMode.HALF_UP);
        toEntities.remove(fromEntity);
        StringBuffer message = new StringBuffer();
        toEntities.forEach(toEntity -> {
            ExpenseInfo info = new ExpenseInfo(
                    roomEntity.getToken(),
                    amountForEach.compareTo(BigDecimal.ZERO) < 0 ? toEntity.getUserUid() : fromEntity.getUserUid(),
                    amountForEach.compareTo(BigDecimal.ZERO) < 0 ? fromEntity.getUserUid() : toEntity.getUserUid(),
                    mathHelper.round(amountForEach.abs()),
                    validated.getDescription());

            ExpenseEntity result = expenseService.expense(info, roomEntity);
            String answerMessage = String.format(
                    "Expense *%.2f*%s to user *%s* has been successfully added%s\n",
                    result.getAmount(),
                    roomEntity.getCurrency() == null
                            ? ""
                            : " " + roomEntity.getCurrency().getSymbol() + " "
                                    + roomEntity.getCurrency().getCode(),
                    result.getTo().getUsername(),
                    result.getName().isBlank() ? "" : ", description: " + result.getName());
            message.append(answerMessage);
        });

        return ResultK.Ok(new CommandProcessed(validated.getChatId(), message.toString()));
    }

    private RoomUserEntity filterRoomUser(RoomEntity room, String username) {
        return room.getUsers().stream()
                .filter(entity -> entity.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
