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
    private String[] messageParts1 = new String[]{"/push", "username", "100", "expenseName"};
    private String[] messageParts2 = new String[]{"/push", "username", "100", "10%"};
    private String[] messageParts3 = new String[]{"/push", "username", "100", "expenseName", "10%"};
    private BigDecimal initialCalculatedAmount = BigDecimal.valueOf(100.0);
    private String expectedName = "expenseName";
    private String expectedEmptyName = StringUtils.EMPTY;

    @Test
    void getNameAndCalculatedAmount1() {
        // Case [0]/push [1]@username [2]math_expr [3]expName
        NameAndCalculatedAmount nameAndCalculatedAmount = pushHelper.getNameAndCalculatedAmount(
                messageParts1, initialCalculatedAmount);

        assertEquals(expectedName, nameAndCalculatedAmount.name());
        assertEquals(initialCalculatedAmount, nameAndCalculatedAmount.calculatedAmount());
    }

    @Test
    void getNameAndCalculatedAmount2() {
        // Case [0]/push [1]@username [2]math_expr [3]percentage
        NameAndCalculatedAmount nameAndCalculatedAmount = pushHelper.getNameAndCalculatedAmount(
                messageParts2, initialCalculatedAmount);

        assertEquals(expectedEmptyName, nameAndCalculatedAmount.name());
        assertEquals(initialCalculatedAmount.add(BigDecimal.valueOf(10)), nameAndCalculatedAmount.calculatedAmount());
    }

    @Test
    void getNameAndCalculatedAmount3() {
        // Case [0]/push [1]@username [2]math_expr [3]expName [4]Percentage
        NameAndCalculatedAmount nameAndCalculatedAmount = pushHelper.getNameAndCalculatedAmount(
                messageParts3, initialCalculatedAmount);

        assertEquals(expectedName, nameAndCalculatedAmount.name());
        assertEquals(initialCalculatedAmount.add(BigDecimal.valueOf(10)), nameAndCalculatedAmount.calculatedAmount());
    }
}