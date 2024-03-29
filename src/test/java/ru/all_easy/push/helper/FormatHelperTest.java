package ru.all_easy.push.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import ru.all_easy.push.common.UnitTest;

class FormatHelperTest extends UnitTest {

    @Spy
    private MathHelper mathHelper = new MathHelper(new DoubleEvaluator());

    @InjectMocks
    private FormatHelper formatHelper;

    @Test
    void checkRemovingZerosFromResult() {
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("person1,person2", BigDecimal.valueOf(22));
        result.put("person1,person3", BigDecimal.ZERO);
        result.put("person2,person3", BigDecimal.ZERO);
        result.put("person4,person5", BigDecimal.valueOf(1000));

        String actual = formatHelper.formatResult(result);

        String expected =
                """
        *person4* owes *person5* sum: *1000.00*
        *person1* owes *person2* sum: *22.00*""";

        assertEquals(expected, actual);
    }

    @Test
    void checkRemovingZerosUnderPrecisionFromResult() {
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("person1,person2", BigDecimal.valueOf(22));
        result.put("person1,person3", BigDecimal.valueOf(0.000123));
        result.put("person1,person5", BigDecimal.valueOf(0.001123));
        result.put("person1,person9", BigDecimal.valueOf(0.000092131));
        result.put("person2,person3", BigDecimal.ZERO);
        result.put("person4,person5", BigDecimal.valueOf(1000));

        String actual = formatHelper.formatResult(result);

        String expected =
                """
        *person4* owes *person5* sum: *1000.00*
        *person1* owes *person2* sum: *22.00*""";

        assertEquals(expected, actual);
    }
}
