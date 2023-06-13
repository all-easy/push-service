package ru.all_easy.push.currency.repository.model;

import javax.persistence.*;
import java.io.Serializable;

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

    public String getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }
}
