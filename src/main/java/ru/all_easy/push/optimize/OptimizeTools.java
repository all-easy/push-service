package ru.all_easy.push.optimize;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.helper.MathHelper;

@Component
public class OptimizeTools {

    private static final String SEPARATOR = ",";

    private final MathHelper mathHelper;

    public OptimizeTools(MathHelper mathHelper) {
        this.mathHelper = mathHelper;
    }

    public Set<OweInfo> getOwes(String to, Map<String, BigDecimal> optExpenses) {
        return optExpenses.entrySet().stream()
                .filter(entry -> entry.getKey().contains(to))
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) > 0)
                .map(entry -> {
                    var keyList = entry.getKey().split(",");
                    return new OweInfo(keyList[0], keyList[1], entry.getValue());
                })
                .collect(Collectors.toSet());
    }

    public Map<String, BigDecimal> optimize(List<ExpenseEntity> roomExpenses) {
        var grouped = new HashMap<String, BigDecimal>();

        roomExpenses.forEach(expense -> {
            var key = expense.getFromUid() + SEPARATOR + expense.getToUid();
            if (grouped.get(key) == null) {
                grouped.put(key, expense.getAmount());
                return;
            }

            grouped.computeIfPresent(key, (k, v) -> mathHelper.add(v, expense.getAmount()));
        });

        Map<String, BigDecimal> collapsed = new HashMap<>();
        grouped.forEach((key, value) -> {
            var mirroredKey = mirroredKey(key);
            var mirroredSum = grouped.get(mirroredKey);
            if (mirroredSum == null) {
                mirroredSum = BigDecimal.ZERO;
            }
            var result = mathHelper.subtract(value, mirroredSum);
            if (result.doubleValue() < 0) {
                collapsed.put(key, BigDecimal.ZERO);
            } else {
                collapsed.put(key, result);
            }
        });

        Map<String, BigDecimal> correct = new HashMap<>();
        collapsed.forEach((key, value) -> {
            String[] split = key.split(SEPARATOR);
            correct.put(split[1] + SEPARATOR + split[0], value);
        });

        return correct;
    }

    private String mirroredKey(String key) {
        var ids = key.split(SEPARATOR);
        return ids[1] + SEPARATOR + ids[0];
    }
}
