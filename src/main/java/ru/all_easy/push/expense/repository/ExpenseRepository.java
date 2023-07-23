package ru.all_easy.push.expense.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ExpenseRepository extends R2dbcRepository<ExpenseEntity, Long> {

    Mono<List<ExpenseEntity>> findAllByRoomTokenAndCurrency(String roomToken, String currency);

    Mono<Page<ExpenseEntity>> findAllByRoomToken(String roomToken, PageRequest pageRequest);
}
