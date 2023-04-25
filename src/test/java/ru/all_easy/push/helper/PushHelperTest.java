package ru.all_easy.push.helper;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.all_easy.push.common.UnitTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PushHelperTest extends UnitTest {
    @InjectMocks
    private PushHelper pushHelper;
    private BigDecimal initialCalculatedAmount = BigDecimal.valueOf(100.0);
    private String expectedName = "expenseName";
    private String expectedEmptyName = StringUtils.EMPTY;

    @Test
    void getNameAndCalculatedAmount_PushAmountWithExpenseNameWithoutPercentage() {
        // Case [0]/push [1]@username [2]math_expr [3]expName
        String[] messageParts = new String[]{"/push", "username", "100", "expenseName"};
        NameAndCalculatedAmount nameAndCalculatedAmount = pushHelper.getNameAndCalculatedAmount(
                messageParts, initialCalculatedAmount);

        assertEquals(expectedName, nameAndCalculatedAmount.name());
        assertEquals(initialCalculatedAmount, nameAndCalculatedAmount.calculatedAmount());
    }

    @Test
    void getNameAndCalculatedAmount_PushAmountWithPercentageAndWithoutExpenseName() {
        // Case [0]/push [1]@username [2]math_expr [3]percentage
        String[] messageParts = new String[]{"/push", "username", "100", "10%"};
        NameAndCalculatedAmount nameAndCalculatedAmount = pushHelper.getNameAndCalculatedAmount(
                messageParts, initialCalculatedAmount);

        assertEquals(expectedEmptyName, nameAndCalculatedAmount.name());
        assertEquals(initialCalculatedAmount.add(BigDecimal.valueOf(10)), nameAndCalculatedAmount.calculatedAmount());
    }

    @Test
    void getNameAndCalculatedAmount_PushAmountWithExpenseNameAndPercentage() {
        // Case [0]/push [1]@username [2]math_expr [3]expName [4]Percentage
        String[] messageParts = new String[]{"/push", "username", "100", "expenseName", "10%"};
        NameAndCalculatedAmount nameAndCalculatedAmount = pushHelper.getNameAndCalculatedAmount(
                messageParts, initialCalculatedAmount);

        assertEquals(expectedName, nameAndCalculatedAmount.name());
        assertEquals(initialCalculatedAmount.add(BigDecimal.valueOf(10)), nameAndCalculatedAmount.calculatedAmount());
    }
}