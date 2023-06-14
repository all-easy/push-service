package ru.all_easy.push.helper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.all_easy.push.common.UnitTest;

class MathHelperUnitTest extends UnitTest {

    @InjectMocks
    private MathHelper mathHelper;

    private final BigDecimal val1 = BigDecimal.valueOf(0.000123);
    private final BigDecimal val2 = BigDecimal.valueOf(0.000111);

    @Test
    void checkValuesAreEqualWithDelta0001() {
        boolean equal = mathHelper.equalWithDelta(val1, val2, 0.0001);
        assertTrue(equal);
    }

    @Test
    void checkValuesAreNotEqualWithDelta00001() {
        boolean equal = mathHelper.equalWithDelta(val1, val2, 0.00001);
        assertFalse(equal);
    }
}
