package ru.all_easy.push.currency.repository.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "currency")
public class CurrencyEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true)
    String code;

    @Column(name = "symbol", unique = true)
    String symbol;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
