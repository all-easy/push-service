package ru.all_easy.push.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {
    CurrencyEntity findByCode(String code);
}
