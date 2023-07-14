package ru.all_easy.push.telegram.commands.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
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
import ru.all_easy.push.telegram.commands.validators.SplitCommandValidator;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

/**
 * Implements main logic of the split command. In particular, adds new transaction records.
 * Default isolation level is applied (Read Committed for PostgreSQL).
 *
 * @see <a href="https://www.postgresql.org/docs/current/transaction-iso.html#:~:text=Read%20Committed%20is%20the%20default,query%20execution%20by%20concurrent%20transactions.">
 *     PostgreSQL Read Committed (Default) Isolation Level
 *     </a>
 * @see Isolation
 *
 * @see SplitCommandValidator
 * @see SplitCommandValidated
 */
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

    @Transactional()
    public ResultK<CommandProcessed, CommandError> split(SplitCommandValidated validated) {

        RoomEntity roomEntity = roomService.findByToken(String.valueOf(validated.getChatId()));
        if (roomEntity == null)
            return ResultK.Err(
                    new CommandError(validated.getChatId(), AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage()));
        if (roomEntity.getCurrency() == null)
            return ResultK.Err(
                    new CommandError(validated.getChatId(), AnswerMessageTemplate.UNSET_CURRENCY.getMessage()));

        RoomUserEntity fromEntity = getFromEntity(roomEntity, validated.getFromUsername());
        if (fromEntity == null)
            return ResultK.Err(new CommandError(
                    validated.getChatId(),
                    String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.getFromUsername())));

        List<RoomUserEntity> toEntities = getToEntities(roomEntity, fromEntity);

        String message = addTransactionsAndGetMessage(validated, roomEntity, fromEntity, toEntities);

        return ResultK.Ok(new CommandProcessed(validated.getChatId(), message));
    }

    private RoomUserEntity getFromEntity(RoomEntity room, String username) {
        return room.getUsers().stream()
                .filter(entity -> entity.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private List<RoomUserEntity> getToEntities(RoomEntity roomEntity, RoomUserEntity fromEntity) {
        return roomEntity.getUsers().stream()
                .filter(roomUserEntity -> !roomUserEntity.getUserUid().equals(fromEntity.getUserUid()))
                .sorted(Comparator.comparing(
                        roomUserEntity -> roomUserEntity.getUser().getUsername()))
                .toList();
    }

    private String addTransactionsAndGetMessage(
            SplitCommandValidated validated,
            RoomEntity roomEntity,
            RoomUserEntity fromEntity,
            List<RoomUserEntity> toEntities) {

        BigDecimal amountForEach =
                validated.getAmount().abs().divide(BigDecimal.valueOf(toEntities.size() + 1), 2, RoundingMode.HALF_UP);

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

        return message.toString();
    }
}
