package ru.all_easy.push.helper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.all_easy.push.common.UnitTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatHelperTest extends UnitTest {

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

        String expected = """
        *person4* owes *person5* sum: *1000.0*
        *person1* owes *person2* sum: *22.0*""";

        assertEquals(expected, actual);
    }
  
}