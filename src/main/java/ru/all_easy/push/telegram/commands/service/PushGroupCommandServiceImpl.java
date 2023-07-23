package ru.all_easy.push.telegram.commands.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
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

    public PushGroupCommandServiceImpl(
            RoomService roomService, UserService userService, ExpenseService expenseService, MathHelper mathHelper) {
        this.roomService = roomService;
        this.userService = userService;
        this.expenseService = expenseService;
        this.mathHelper = mathHelper;
    }

    @Override
    public Mono<ResultK> push(PushCommandValidated validated) {
        Mono<RoomEntity> roomEntityMono = roomService.findByToken(String.valueOf(validated.chatId()));
        Mono<UserEntity> fromEntityMono = userService.findUserInRoomByUsername(
                String.valueOf(validated.chatId()), validated.fromUsername());
        Mono<UserEntity> toEntityMono =
                userService.findUserInRoomByUsername(String.valueOf(validated.chatId()), validated.toUsername());
        return Mono.zip(roomEntityMono, fromEntityMono, toEntityMono).flatMap(tuple -> {
            var roomEntity = tuple.getT1();
            var fromEntity = tuple.getT2();
            var toEntity = tuple.getT3();
            if (roomEntity == null) {
                return Mono.just(
                        ResultK.Err(new PushCommandServiceError(validated.chatId(), AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage())));
            }
            if (roomEntity.getCurrency() == null) {
                return Mono.just(
                        ResultK.Err(new PushCommandServiceError(validated.chatId(), AnswerMessageTemplate.UNSET_CURRENCY.getMessage())));
            }
            if (fromEntity == null) {
                return Mono.just(ResultK.Err(new PushCommandServiceError(validated.chatId(),
                        String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.fromUsername()))));
            }
            if (toEntity == null) {
                return Mono.just(ResultK.Err(new PushCommandServiceError(validated.chatId(),
                        String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.toUsername()))));
            }

            ExpenseInfo info = new ExpenseInfo(
                    roomEntity.getToken(),
                    validated.amount().compareTo(BigDecimal.ZERO) < 0 ? toEntity.getUid() : fromEntity.getUid(),
                    validated.amount().compareTo(BigDecimal.ZERO) < 0 ? fromEntity.getUid() : toEntity.getUid(),
                    mathHelper.round(validated.amount().abs()),
                    validated.name());

            return expenseService.expense(info, roomEntity).map(result -> {
                String answerMessage = String.format(
                        "Expense *%.2f*%s to user *%s* has been successfully added%s",
                        result.getAmount(),
                        roomEntity.getCurrency() == null
                                ? ""
                                : " " + roomEntity.getCurrency().getSymbol() + " "
                                        + roomEntity.getCurrency().getCode(),
                        result.getTo().getUsername(),
                        result.getName().isBlank() ? "" : ", description: " + result.getName());

                return ResultK.Ok(new PushCommandServiceResult(validated.chatId(), null, answerMessage, null));
            });
        });
    }
}
