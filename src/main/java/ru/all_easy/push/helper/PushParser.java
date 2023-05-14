package ru.all_easy.push.helper;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PushParser {

    private static final String SLASH = "/";
    private static final String DOG = "@";
    private static final String PERCENT = "%";

    private final MathHelper mathHelper;

    public PushParser(MathHelper mathHelper) {
        this.mathHelper = mathHelper;
    }

    public String extractName(List<String> messageParts) {
        return messageParts.stream()
            .filter(part -> !part.contains(SLASH)
                && !part.contains(DOG)
                && !part.contains(PERCENT))
            .collect(Collectors.joining(" "));
    }

    public BigDecimal addPercentToMathExpression(String mathExpression, int percent) {
        var calculated = mathHelper.calculate(mathExpression);
        return mathHelper.addPercent(percent, calculated);
    }

    public int extractPercent(List<String> messageParts) {
        var percent = messageParts.stream()
            .filter(part -> part.contains(PERCENT))
            .findFirst();
        return percent
            .map(it -> NumberUtils.toInt(it.replace(PERCENT, "")))
            .orElse(0);
    }
}
