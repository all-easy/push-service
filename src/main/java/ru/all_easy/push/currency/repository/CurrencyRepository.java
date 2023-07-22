package ru.all_easy.push.currency.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;

public interface CurrencyRepository extends R2dbcRepository<CurrencyEntity, Long> {
    Mono<CurrencyEntity> findByCode(String code);
}
