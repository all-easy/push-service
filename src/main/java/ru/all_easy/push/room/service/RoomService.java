package ru.all_easy.push.room.service;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.optimize.OptimizeTools;
import ru.all_easy.push.optimize.OweInfo;
import ru.all_easy.push.room.repository.RoomRepository;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.exception.RoomServiceException;
import ru.all_easy.push.room.service.model.RoomInfo;
import ru.all_easy.push.room.service.model.RoomResult;
import ru.all_easy.push.user.repository.UserEntity;

@Service
public class RoomService {

    private final RoomRepository repository;
    private final ExpenseService expenseService;

    private final OptimizeTools optimizeTools;

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    public RoomService(RoomRepository repository, ExpenseService expenseService, OptimizeTools optimizeTools) {
        this.repository = repository;
        this.expenseService = expenseService;
        this.optimizeTools = optimizeTools;
    }

    public Mono<RoomEntity> createRoomEntity(RoomInfo roomInfo) {
        return findByToken(roomInfo.token()).switchIfEmpty(Mono.defer(() -> createAndSaveRoom(roomInfo)));
    }

    private Mono<RoomEntity> createAndSaveRoom(RoomInfo roomInfo) {
        RoomEntity room = new RoomEntity().setTitle(roomInfo.title()).setToken(roomInfo.token());
        return repository.save(room);
    }

    @Transactional
    public Mono<RoomResult> enterRoom(UserEntity user, RoomEntity room) {

        return Mono.just(new RoomResult(null, room.getToken(), room.getTitle(), null, null));
    }

    public Mono<Set<OweInfo>> optimize(String username, String roomToken) {
        return repository
                .findByToken(roomToken)
                .flatMap(expenseService::optimize)
                .map(optimized -> optimizeTools.getOwes(username, optimized));
    }

    public Mono<RoomEntity> findRoomByToken(String token) {
        return findByToken(token)
                .switchIfEmpty(Mono.error(new RoomServiceException()
                        .setMessage("Room with token: " + token + " not found")
                        .setCode(404)));
    }

    public Mono<RoomEntity> findByToken(String token) {
        return repository.findByToken(token);
    }

    public Mono<String> getRoomCurrency(String token) {
        return repository.findRoomCurrency(token);
    }

    public Mono<Void> setRoomCurrency(Long chatId, CurrencyEntity currency) {
        return findRoomByToken(String.valueOf(chatId))
                .flatMap(room -> {
                    room.setCurrency(currency);
                    return repository.save(room);
                })
                .then();
    }

    @Transactional
    public void autoMigrateToSupergroup(String oldToken, String newToken) {
        repository
                .findByToken(oldToken)
                .map(oldRoomEntity -> {
                    RoomEntity newRoomEntity = new RoomEntity();
                    newRoomEntity.setTitle(oldRoomEntity.getTitle());
                    if (oldRoomEntity.getCurrency() != null) newRoomEntity.setCurrency(oldRoomEntity.getCurrency());
                    newRoomEntity.setToken(newToken);
                    return newRoomEntity;
                })
                .flatMap(repository::save)
                .flatMap(savedRoom -> {
                    repository.updateExpenseRoomToken(oldToken, newToken);
                    repository.updateRoomUserRoomToken(oldToken, newToken);
                    return repository.deleteRoomByRoomToken(oldToken);
                })
                .subscribe();
    }
}
