package ru.all_easy.push.expense.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.repository.ExpenseRepository;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.expense.service.model.ExpenseInfoDateTime;
import ru.all_easy.push.helper.DateTimeHelper;
import ru.all_easy.push.optimize.OptimizeTools;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.service.UserService;

@Service
@Primary
public class ExpenseServiceImpl implements ExpenseService {
    private final OptimizeTools optimizeTools;
    private final UserService userService;
    private final DateTimeHelper dateTimeHelper;
    private final ExpenseRepository repository;

    public ExpenseServiceImpl(
            OptimizeTools optimizeTools,
            UserService userService,
            DateTimeHelper dateTimeHelper,
            ExpenseRepository repository) {
        this.optimizeTools = optimizeTools;
        this.userService = userService;
        this.dateTimeHelper = dateTimeHelper;
        this.repository = repository;
    }

    @Override
    public Mono<Map<String, BigDecimal>> optimize(RoomEntity room) {
        return findRoomExpensesByCurrency(room).map(optimizeTools::optimize);
    }

    @Transactional
    @Override
    public Mono<ExpenseEntity> expense(ExpenseInfo expenseInfo, RoomEntity room) {
        Mono<UserEntity> fromMono = userService.findUserByUid(expenseInfo.fromUid());
        Mono<UserEntity> toMono = userService.findUserByUid(expenseInfo.toUid());

        return Mono.zip(fromMono, toMono).flatMap(tuple -> {
            UserEntity from = tuple.getT1();
            UserEntity to = tuple.getT2();

            ExpenseEntity expense = new ExpenseEntity()
                    .setRoomToken(room.getToken())
                    .setToUid(to.getUid())
                    .setFromUid(from.getUid())
                    .setName(expenseInfo.name())
                    .setDateTime(dateTimeHelper.now())
                    .setAmount(expenseInfo.amount())
                    .setCurrency(room.getCurrency());

            return repository.save(expense);
        });
    }

    public Mono<List<ExpenseInfoDateTime>> findLimitRoomExpenses(String roomToken, Integer limit) {
        return Mono.empty();
        //        return repository
        //                .findAllByRoomToken(roomToken, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC,
        // "dateTime")))
        //                .flatMapMany(page -> Flux.fromIterable(page.getContent()))
        //                .map(p -> new ExpenseInfoDateTime(
        //                        p.getFromUid().getUsername(),
        //                        p.getToUid().getUsername(),
        //                        p.getAmount(),
        //                        p.getName(),
        //                        p.getDateTime(),
        //                        p.getCurrency() + " " + p.getCurrency()))
        //                .sort(Comparator.comparing(ExpenseInfoDateTime::dateTime))
        //                .collectList();
    }

    public Mono<List<ExpenseEntity>> findRoomExpensesByCurrency(RoomEntity room) {
        return repository.findAllByRoomTokenAndCurrency(room.getToken(), room.getCurrency());
    }
}
