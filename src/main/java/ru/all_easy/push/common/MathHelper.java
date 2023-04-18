package ru.all_easy.push.common;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.fathzer.soft.javaluator.DoubleEvaluator;

@Component
public class MathHelper {

    private final DoubleEvaluator doubleEvaluator;

    public MathHelper(DoubleEvaluator doubleEvaluator) {
        this.doubleEvaluator = doubleEvaluator;
    }

    public BigDecimal calculate(String amountStr) {
        Double evaluated = doubleEvaluator.evaluate(amountStr);
        return BigDecimal.valueOf(evaluated);
    }
    
}
