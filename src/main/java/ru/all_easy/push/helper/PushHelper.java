package ru.all_easy.push.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class PushHelper {
    public NameAndAmountWithPercents getNameAndCalculatedAmount(String[] messageParts, BigDecimal calculatedAmount) {
        String name = StringUtils.EMPTY;

        // Case [0]/push [1]@username [2]math_expr [3]expNameOrPercentage?
        if (messageParts.length == 4) {
            Optional<Integer> percentageNumberOptional = validatePercentage(messageParts[3]);
            if (percentageNumberOptional.isPresent()) {
                int percentageNumber = percentageNumberOptional.get();
                calculatedAmount = BigDecimal.valueOf(calculatedAmount.doubleValue() * (100 + percentageNumber) / 100);
                name = StringUtils.EMPTY;
            } else {
                name = messageParts[3];
            }
        }

        // Case [0]/push [1]@username [2]math_expr [3]expName [4]Percentage
        if (messageParts.length == 5) {
            name = messageParts[3];
            Optional<Integer> percentageNumberOptional = validatePercentage(messageParts[4]);
            if (percentageNumberOptional.isPresent()) {
                int percentageNumber = percentageNumberOptional.get();
                calculatedAmount = BigDecimal.valueOf(calculatedAmount.doubleValue() * (100 + percentageNumber) / 100);
            } else {
                throw new IllegalArgumentException();
            }
        }

        return new NameAndAmountWithPercents(name, calculatedAmount);
    }

    private Optional<Integer> validatePercentage(String value) {
        if (!value.contains("%") || value.isBlank() || value.equals(" ")) return Optional.empty();

        String valueStr = value.replace("%", "");
        for (int i = 0; i < valueStr.length(); i++) {
            if (!Character.isDigit(valueStr.charAt(i))) return Optional.empty();
        }

        int valueInt = Integer.parseInt(valueStr);
        if (valueInt <= 0 || valueInt > 100) return Optional.empty();

        return Optional.of(valueInt);
    }
}
