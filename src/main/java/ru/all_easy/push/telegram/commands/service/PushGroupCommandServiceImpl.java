package ru.all_easy.push.telegram.commands.service;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.telegram.commands.service.model.PushCommandServiceError;
import ru.all_easy.push.telegram.commands.service.model.PushCommandServiceResult;
import ru.all_easy.push.telegram.commands.validators.model.PushCommandValidated;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.service.UserService;

@Service
public class PushGroupCommandServiceImpl implements PushGroupCommandService {

    private final RoomService roomService;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final MathHelper mathHelper;

    private static final Logger logger = LoggerFactory.getLogger(PushGroupCommandServiceImpl.class);

    public PushGroupCommandServiceImpl(
            RoomService roomService, UserService userService, ExpenseService expenseService, MathHelper mathHelper) {
        this.roomService = roomService;
        this.userService = userService;
        this.expenseService = expenseService;
        this.mathHelper = mathHelper;
    }

    @Override
    public Mono<ResultK> push(PushCommandValidated validated) {
        return roomService.findByToken(String.valueOf(validated.chatId())).flatMap(roomEntity -> {
            if (roomEntity == null) {
                return Mono.just(ResultK.Err(new PushCommandServiceError(
                        validated.chatId(), AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage())));
            }
            if (roomEntity.getCurrency() == null) {
                return Mono.just(ResultK.Err(new PushCommandServiceError(
                        validated.chatId(), AnswerMessageTemplate.UNSET_CURRENCY.getMessage())));
            }

            return userService
                    .findUserInRoomByUsername(String.valueOf(validated.chatId()), validated.fromUsername())
                    .defaultIfEmpty(new UserEntity()) // Provide a default UserEntity if not found
                    .flatMap(fromEntity -> {
                        if (fromEntity.getUid() == null) {
                            return Mono.just(ResultK.Err(new PushCommandServiceError(
                                    validated.chatId(),
                                    String.format(
                                            AnswerMessageTemplate.UNADDED_USER.getMessage(),
                                            validated.fromUsername()))));
                        }

                        return userService
                                .findUserInRoomByUsername(String.valueOf(validated.chatId()), validated.toUsername())
                                .defaultIfEmpty(new UserEntity()) // Provide a default UserEntity if not found
                                .flatMap(toEntity -> {
                                    if (toEntity.getUid() == null) {
                                        return Mono.just(ResultK.Err(new PushCommandServiceError(
                                                validated.chatId(),
                                                String.format(
                                                        AnswerMessageTemplate.UNADDED_USER.getMessage(),
                                                        validated.toUsername()))));
                                    }

                                    ExpenseInfo info = new ExpenseInfo(
                                            roomEntity.getToken(),
                                            validated.amount().compareTo(BigDecimal.ZERO) < 0
                                                    ? toEntity.getUid()
                                                    : fromEntity.getUid(),
                                            validated.amount().compareTo(BigDecimal.ZERO) < 0
                                                    ? fromEntity.getUid()
                                                    : toEntity.getUid(),
                                            mathHelper.round(validated.amount().abs()),
                                            validated.name());

                                    return expenseService
                                            .expense(info, roomEntity)
                                            .map(result -> {
                                                String answerMessage = String.format(
                                                        "Expense *%.2f*%s to user *%s* has been successfully added%s",
                                                        result.getAmount(),
                                                        roomEntity.getCurrency() == null
                                                                ? ""
                                                                : " " + roomEntity.getCurrency() + " "
                                                                        + roomEntity.getCurrency(),
                                                        result.getToUid(),
                                                        result.getName().isBlank()
                                                                ? ""
                                                                : ", description: " + result.getName());

                                                return ResultK.Ok(new PushCommandServiceResult(
                                                        validated.chatId(), null, answerMessage, null));
                                            });
                                });
                    });
        });
    }
}
