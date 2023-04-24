package ru.all_easy.push.helper;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FormatHelper {

    private final MathHelper mathHelper;

    public FormatHelper(MathHelper mathHelper) {
        this.mathHelper = mathHelper;
    }

    public String formatResult(Map<String, BigDecimal> result) {
        return result.entrySet().stream()
            .filter(set -> !mathHelper.equalWithDelta(set.getValue(), BigDecimal.ZERO, 0.0001))
            .map(set -> {
                String[] participants = set.getKey().split(",");
                BigDecimal sum = set.getValue();

                return String.format("*%s* owes *%s* sum: *%.4f*",
                        participants[0],
                        participants[1],
                        sum.doubleValue());
            }).collect(Collectors.joining("\n"));
    }
}
