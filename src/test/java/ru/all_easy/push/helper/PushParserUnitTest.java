package ru.all_easy.push.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import ru.all_easy.push.common.UnitTest;

class PushParserUnitTest extends UnitTest {

    @Spy
    private MathHelper mathHelper = new MathHelper(new DoubleEvaluator());

    @InjectMocks
    private PushParser pushParser;

    @Test
    void extractNameWithBasePushCommandOrder() {
        var message = "/push @username 100 name";
        List<String> messageParts = Arrays.stream(message.split(" ")).toList();
        var extractName = pushParser.extractName(messageParts.subList(3, messageParts.size()));

        assertEquals("name", extractName);
    }

    @Test
    void calculateAmountWithPercentsAndBasePushCommandOrder() {
        var message = "/push @username 124 18%";
        var messageParts = Arrays.stream(message.split(" ")).toList();
        int percent = pushParser.extractPercent(messageParts.subList(2, messageParts.size()));

        var calculated = pushParser.addPercentToMathExpression(messageParts.get(2), percent);

        assertEquals(BigDecimal.valueOf(146.32), calculated);
    }

    @Test
    void calculateAmountWithPercentsAndMathExpression() {
        var message = "/push @username 100+20 18%";
        var messageParts = Arrays.stream(message.split(" ")).toList();
        int percent = pushParser.extractPercent(messageParts.subList(2, messageParts.size()));

        var calculated = pushParser.addPercentToMathExpression(messageParts.get(2), percent);

        assertEquals(BigDecimal.valueOf(141.6), calculated);
    }

    @Test
    void calculateAmountWithPercentsAndInvalidMathExpression() {
        var message = "/push @username 18% 100+20";

        assertThrows(IllegalArgumentException.class, () -> {
            var messageParts = Arrays.stream(message.split(" ")).toList();
            int percent = pushParser.extractPercent(messageParts.subList(2, messageParts.size()));

            pushParser.addPercentToMathExpression(messageParts.get(2), percent);
        });
    }

    @Test
    void calculateAmountWithNameAndPercents() {
        var message = "/push @username 100+20 name 18%";

        var messageParts = Arrays.stream(message.split(" ")).toList();
        var additionalInfo = messageParts.subList(3, messageParts.size());
        int percent = pushParser.extractPercent(additionalInfo);
        BigDecimal calculated = pushParser.addPercentToMathExpression(messageParts.get(2), percent);

        var name = pushParser.extractName(additionalInfo);

        assertEquals(BigDecimal.valueOf(141.6), calculated);
        assertEquals("name", name);
    }

    @Test
    void calculateAmountWithPercentsAndName() {
        var message = "/push @username 100+20 18% name";

        var messageParts = Arrays.stream(message.split(" ")).toList();
        var additionalInfo = messageParts.subList(3, messageParts.size());
        int percent = pushParser.extractPercent(additionalInfo);
        BigDecimal calculated = pushParser.addPercentToMathExpression(messageParts.get(2), percent);

        var name = pushParser.extractName(additionalInfo);

        assertEquals(BigDecimal.valueOf(141.6), calculated);
        assertEquals("name", name);
    }

    @Test
    void calculateAmountWithPercentsAndNameWithSpaces() {
        var message = "/push @username 100+20 18% name1 name2 name3";

        var messageParts = Arrays.stream(message.split(" ")).toList();
        var additionalInfo = messageParts.subList(3, messageParts.size());

        var name = pushParser.extractName(additionalInfo);

        assertEquals("name1 name2 name3", name);
    }

    @Test
    void calculateAmountWithNameWithSpacesAndPercents() {
        var message = "/push @username 100+20 name1 name2 name3 18%";

        var messageParts = Arrays.stream(message.split(" ")).toList();
        var additionalInfo = messageParts.subList(3, messageParts.size());

        var name = pushParser.extractName(additionalInfo);

        assertEquals("name1 name2 name3", name);
    }
}
