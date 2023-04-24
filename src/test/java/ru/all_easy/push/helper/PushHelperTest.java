package ru.all_easy.push.helper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PushHelperTest {
    @InjectMocks
    private PushHelper pushHelper;
    private static String[] messageParts1;
    private static String[] messageParts2;
    private static String[] messageParts3;
    private static BigDecimal initialCalculatedAmount;

    @BeforeAll
    static void beforeAll() {
        messageParts1 = new String[]{"/push", "@username", "10+10", "expenseName"};
        messageParts2 = new String[]{"/push", "@username", "10+10", "10%"};
        messageParts3 = new String[]{"/push", "@username", "10+10", "expenseName", "10%"};
        initialCalculatedAmount = BigDecimal.valueOf(100.0);
    }

    @Test
    void getNameAndCalculatedAmount1() {
        // Case [0]/push [1]@username [2]math_expr [3]expName
        NameAndCalculatedAmount nameAndCalculatedAmount = pushHelper.getNameAndCalculatedAmount(
                messageParts1, initialCalculatedAmount);

    }

    @Test
    void getNameAndCalculatedAmount2() {
        // Case [0]/push [1]@username [2]math_expr [3]percentage
    }

    @Test
    void getNameAndCalculatedAmount3() {
        // Case [0]/push [1]@username [2]math_expr [3]expName [4]Percentage
    }
}