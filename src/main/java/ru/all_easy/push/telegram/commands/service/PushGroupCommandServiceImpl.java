package ru.all_easy.push.telegram.commands.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.telegram.commands.service.model.PushCommandServiceError;
import ru.all_easy.push.telegram.commands.validators.model.PushCommandValidated;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

@Service
public class PushGroupCommandServiceImpl implements PushGroupCommandService {

    private final RoomService roomService;
    private final ExpenseService expenseService;

    public PushGroupCommandServiceImpl(RoomService roomService, ExpenseService expenseService) {
        this.roomService = roomService;
        this.expenseService = expenseService;
    }

    @Override
    public ResultK<String, PushCommandServiceError> push(PushCommandValidated validated) {
        RoomEntity roomEntity = roomService.findByToken(String.valueOf(validated.getChatId()));
        if (roomEntity == null) {
            return ResultK.Err(new PushCommandServiceError(AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage()));
        }

        if (roomEntity.getCurrency() == null) {
            return ResultK.Err(new PushCommandServiceError(AnswerMessageTemplate.UNSET_CURRENCY.getMessage()));
        }

        RoomUserEntity fromEntity = filterRoomUser(roomEntity, validated.getFromUsername());
        if (fromEntity == null) {
            return ResultK.Err(new PushCommandServiceError(AnswerMessageTemplate.UNADDED_USER.getMessage()));
        }

        RoomUserEntity toEntity = filterRoomUser(roomEntity, validated.getToUsername());
        if (toEntity == null) {
            return ResultK.Err(new PushCommandServiceError(
                    String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.getToUsername())));
        }

        ExpenseInfo info = new ExpenseInfo(
                roomEntity.getToken(),
                validated.getAmount().compareTo(BigDecimal.ZERO) < 0 ? toEntity.getUserUid() : fromEntity.getUserUid(),
                validated.getAmount().compareTo(BigDecimal.ZERO) < 0 ? fromEntity.getUserUid() : toEntity.getUserUid(),
                validated.getAmount().abs(),
                validated.getName());

        ExpenseEntity result = expenseService.expense(info, roomEntity);
        String answerMessage = String.format(
                "Expense *%.2f*%s to user *%s* has been successfully added%s",
                result.getAmount(),
                roomEntity.getCurrency() == null
                        ? ""
                        : " " + roomEntity.getCurrency().getSymbol() + " "
                                + roomEntity.getCurrency().getCode(),
                result.getTo().getUsername(),
                result.getName().isBlank() ? "" : ", description: " + result.getName());

        return ResultK.Ok(answerMessage);
    }

    private RoomUserEntity filterRoomUser(RoomEntity room, String username) {
        return room.getUsers().stream()
                .filter(entity -> entity.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
