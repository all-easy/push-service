package ru.all_easy.push.helper;

import java.math.BigDecimal;
import java.math.MathContext;

import org.springframework.stereotype.Component;

import com.fathzer.soft.javaluator.DoubleEvaluator;

@Component
public class MathHelper {

    private static final MathContext precision = new MathContext(10);
    private final DoubleEvaluator doubleEvaluator;

    public MathHelper(DoubleEvaluator doubleEvaluator) {
        this.doubleEvaluator = doubleEvaluator;
    }

    public BigDecimal calculate(String amountStr) {
        Double evaluated = doubleEvaluator.evaluate(amountStr, precision);
        return BigDecimal.valueOf(evaluated);
    }

    public BigDecimal add(BigDecimal val1, BigDecimal val2) {
        return val1.add(val2, precision);
    }

    public BigDecimal subtract(BigDecimal val1, BigDecimal val2) {
        return val1.subtract(val2, precision);
    }

    public boolean equalWithDelta(BigDecimal val1, BigDecimal val2, double delta) {
        return val1.subtract(val2, precision).doubleValue() <= delta;
    }
}
