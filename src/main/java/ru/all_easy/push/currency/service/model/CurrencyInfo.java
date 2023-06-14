package ru.all_easy.push.currency.service.model;

public class CurrencyInfo {
    private final String code;
    private final String symbol;

    public CurrencyInfo(String code, String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    public String code() {
        return code;
    }

    public String symbol() {
        return symbol;
    }
}
