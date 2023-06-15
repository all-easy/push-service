package ru.all_easy.push.helper;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class MathHelper {

    private static final MathContext precision = new MathContext(15);
    private static final int places = 2;
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

    public BigDecimal addPercent(int percent, BigDecimal amount) {
        return BigDecimal.valueOf(amount.doubleValue() * (100 + percent) / 100);
    }

    public BigDecimal round(BigDecimal abs) {
        return abs.setScale(places, RoundingMode.HALF_UP);
    }
}
