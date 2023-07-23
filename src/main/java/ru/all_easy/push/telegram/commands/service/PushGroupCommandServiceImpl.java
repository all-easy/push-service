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
import ru.all_easy.push.telegram.commands.validators.model.PushCommandValidated;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

@Service
public class PushGroupCommandServiceImpl implements PushGroupCommandService {

    private final RoomService roomService;
    private final ExpenseService expenseService;
    private final MathHelper mathHelper;

    public PushGroupCommandServiceImpl(RoomService roomService, ExpenseService expenseService, MathHelper mathHelper) {
        this.roomService = roomService;
        this.expenseService = expenseService;
        this.mathHelper = mathHelper;
    }

    @Override
    public Mono<ResultK<String, PushCommandServiceError>> push(PushCommandValidated validated) {
        Mono<RoomEntity> roomEntityMono = roomService.findByToken(String.valueOf(validated.getChatId()));
        Mono<RoomUserEntity> fromEntityMono = filterRoomUser(roomEntityMono, validated.getFromUsername());
        Mono<RoomUserEntity> toEntityMono = filterRoomUser(roomEntityMono, validated.getToUsername());

        return Mono.zip(roomEntityMono, fromEntityMono, toEntityMono).flatMap(tuple -> {
            var roomEntity = tuple.getT1();
            var fromEntity = tuple.getT2();
            var toEntity = tuple.getT3();

            if (roomEntity == null) {
                return Mono.just(
                        ResultK.Err(new PushCommandServiceError(AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage())));
            }

            if (roomEntity.getCurrency() == null) {
                return Mono.just(
                        ResultK.Err(new PushCommandServiceError(AnswerMessageTemplate.UNSET_CURRENCY.getMessage())));
            }

            if (fromEntity == null) {
                return Mono.just(ResultK.Err(new PushCommandServiceError(
                        String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.getFromUsername()))));
            }

            if (toEntity == null) {
                return Mono.just(ResultK.Err(new PushCommandServiceError(
                        String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.getToUsername()))));
            }

            ExpenseInfo info = new ExpenseInfo(
                    roomEntity.getToken(),
                    validated.getAmount().compareTo(BigDecimal.ZERO) < 0
                            ? toEntity.getUserUid()
                            : fromEntity.getUserUid(),
                    validated.getAmount().compareTo(BigDecimal.ZERO) < 0
                            ? fromEntity.getUserUid()
                            : toEntity.getUserUid(),
                    mathHelper.round(validated.getAmount().abs()),
                    validated.getName());

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

                return ResultK.Ok(answerMessage);
            });
        });
    }

    private Mono<RoomUserEntity> filterRoomUser(Mono<RoomEntity> roomMono, String username) {
        return Mono.empty();
    }
}
