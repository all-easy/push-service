package ru.all_easy.push.currency.repository.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;

public class CurrencyEntity implements Serializable {

    @Id
    private Long id;

    String code;

    String symbol;

    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public Long id() {
        return id;
    }
}
