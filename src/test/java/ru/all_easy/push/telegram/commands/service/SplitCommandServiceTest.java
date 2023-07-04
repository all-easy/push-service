package ru.all_easy.push.telegram.commands.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.ExpenseServiceImpl;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.helper.DateTimeHelper;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;
import ru.all_easy.push.user.repository.UserEntity;

@ExtendWith(MockitoExtension.class)
class SplitCommandServiceTest {

    @InjectMocks
    SplitCommandService splitCommandService;

    @Mock
    RoomService roomService;

    @Mock
    ExpenseServiceImpl expenseService;

    @Spy
    MathHelper mathHelper = new MathHelper(new DoubleEvaluator());

    @Spy
    DateTimeHelper dateTimeHelper = new DateTimeHelper();

    @Test
    void split_WithoutDescription_Positive() {
        // Test data
        CurrencyEntity USD = new CurrencyEntity();
        USD.setCode("USD");
        USD.setSymbol("$");

        UserEntity user1 = new UserEntity().setId(1L).setUid("10001").setUsername("user_1");
        UserEntity user2 = new UserEntity().setId(2L).setUid("10002").setUsername("user_2");
        UserEntity user3 = new UserEntity().setId(3L).setUid("10003").setUsername("user_3");

        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(1L).setToken("11111").setTitle("room_title");
        roomEntity.setCurrency(USD);

        RoomUserEntity roomUser1 = new RoomUserEntity()
                .setId(1L)
                .setRoomToken("11111")
                .setUserUid("10001")
                .setUser(user1);
        RoomUserEntity roomUser2 = new RoomUserEntity()
                .setId(2L)
                .setRoomToken("11111")
                .setUserUid("10002")
                .setUser(user2);
        RoomUserEntity roomUser3 = new RoomUserEntity()
                .setId(3L)
                .setRoomToken("11111")
                .setUserUid("10003")
                .setUser(user3);

        roomEntity.setUsers(Set.of(roomUser1, roomUser2, roomUser3));

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(11111L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("");

        // Stubbing
        when(roomService.findByToken("11111")).thenReturn(roomEntity);

        ExpenseInfo expenseInfo1 = new ExpenseInfo("11111", "10001", "10002", BigDecimal.valueOf(33.33), "");
        ExpenseEntity expenseEntity1 = new ExpenseEntity()
                .setName("")
                .setFrom(user1)
                .setTo(user2)
                .setRoom(roomEntity)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(33.33))
                .setCurrency(USD);
        when(expenseService.expense(expenseInfo1, roomEntity)).thenReturn(expenseEntity1);

        ExpenseInfo expenseInfo2 = new ExpenseInfo("11111", "10001", "10003", BigDecimal.valueOf(33.33), "");
        ExpenseEntity expenseEntity2 = new ExpenseEntity()
                .setName("")
                .setFrom(user1)
                .setTo(user3)
                .setRoom(roomEntity)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(33.33))
                .setCurrency(USD);
        when(expenseService.expense(expenseInfo2, roomEntity)).thenReturn(expenseEntity2);

        // Expected
        String responseTextVariation1 =
                """
                Expense *33.33* $ USD to user *user_2* has been successfully added
                Expense *33.33* $ USD to user *user_3* has been successfully added
                """;
        String responseTextVariation2 =
                """
                Expense *33.33* $ USD to user *user_3* has been successfully added
                Expense *33.33* $ USD to user *user_2* has been successfully added
                """;
        String responseText =
                """
                Expense *33.33* $ USD to user *user_2* has been successfully added
                Expense *33.33* $ USD to user *user_3* has been successfully added
                """;
        ResultK<CommandProcessed, CommandError> expectedResult = ResultK.Ok(new CommandProcessed(11111L, responseText));

        // Test
        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);
        assertNull(actualResult.getError());
        assertEquals(11111L, actualResult.getResult().chatId());
        assertTrue(actualResult.getResult().message().equals(responseTextVariation1)
                || actualResult.getResult().message().equals(responseTextVariation2));
        assertNull(actualResult.getResult().replayToId());
        assertNull(actualResult.getResult().replayMarkup());
    }

    @Test
    void split_WithDescription_Positive() {
        // Test data
        CurrencyEntity USD = new CurrencyEntity();
        USD.setCode("USD");
        USD.setSymbol("$");

        UserEntity user1 = new UserEntity().setId(1L).setUid("10001").setUsername("user_1");
        UserEntity user2 = new UserEntity().setId(2L).setUid("10002").setUsername("user_2");
        UserEntity user3 = new UserEntity().setId(3L).setUid("10003").setUsername("user_3");

        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(1L).setToken("11111").setTitle("room_title");
        roomEntity.setCurrency(USD);

        RoomUserEntity roomUser1 = new RoomUserEntity()
                .setId(1L)
                .setRoomToken("11111")
                .setUserUid("10001")
                .setUser(user1);
        RoomUserEntity roomUser2 = new RoomUserEntity()
                .setId(2L)
                .setRoomToken("11111")
                .setUserUid("10002")
                .setUser(user2);
        RoomUserEntity roomUser3 = new RoomUserEntity()
                .setId(3L)
                .setRoomToken("11111")
                .setUserUid("10003")
                .setUser(user3);

        roomEntity.setUsers(Set.of(roomUser1, roomUser2, roomUser3));

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(11111L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("test-description");

        // Stubbing
        when(roomService.findByToken("11111")).thenReturn(roomEntity);

        ExpenseInfo expenseInfo1 =
                new ExpenseInfo("11111", "10001", "10002", BigDecimal.valueOf(33.33), "test-description");
        ExpenseEntity expenseEntity1 = new ExpenseEntity()
                .setName("test-description")
                .setFrom(user1)
                .setTo(user2)
                .setRoom(roomEntity)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(33.33))
                .setCurrency(USD);
        when(expenseService.expense(expenseInfo1, roomEntity)).thenReturn(expenseEntity1);

        ExpenseInfo expenseInfo2 =
                new ExpenseInfo("11111", "10001", "10003", BigDecimal.valueOf(33.33), "test-description");
        ExpenseEntity expenseEntity2 = new ExpenseEntity()
                .setName("test-description")
                .setFrom(user1)
                .setTo(user3)
                .setRoom(roomEntity)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(33.33))
                .setCurrency(USD);
        when(expenseService.expense(expenseInfo2, roomEntity)).thenReturn(expenseEntity2);

        // Expected
        String responseTextVariation1 =
                """
                Expense *33.33* $ USD to user *user_2* has been successfully added, description: test-description
                Expense *33.33* $ USD to user *user_3* has been successfully added, description: test-description
                """;
        String responseTextVariation2 =
                """
                Expense *33.33* $ USD to user *user_3* has been successfully added, description: test-description
                Expense *33.33* $ USD to user *user_2* has been successfully added, description: test-description
                """;

        // Test
        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);
        assertNull(actualResult.getError());

        assertEquals(11111L, actualResult.getResult().chatId());
        assertTrue(actualResult.getResult().message().equals(responseTextVariation1)
                || actualResult.getResult().message().equals(responseTextVariation2));
        assertNull(actualResult.getResult().replayToId());
        assertNull(actualResult.getResult().replayMarkup());
    }

    @Test
    void split_RoomIsNull_Negative() {
        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(55555L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("test-description");

        when(roomService.findByToken("55555")).thenReturn(null);

        ResultK<CommandProcessed, CommandError> expectedResult =
                ResultK.Err(new CommandError(55555L, AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage()));

        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);

        assertNull(actualResult.getResult());
        assertEquals(expectedResult.getError().chatId(), actualResult.getError().chatId());
        assertEquals(
                expectedResult.getError().message(), actualResult.getError().message());
    }

    @Test
    void split_CurrencyIsNull_Negative() {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(1L).setToken("55555").setTitle("room_title");

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(55555L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("test-description");

        when(roomService.findByToken("55555")).thenReturn(roomEntity);

        ResultK<CommandProcessed, CommandError> expectedResult =
                ResultK.Err(new CommandError(55555L, AnswerMessageTemplate.UNSET_CURRENCY.getMessage()));

        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);

        assertNull(actualResult.getResult());
        assertEquals(expectedResult.getError().chatId(), actualResult.getError().chatId());
        assertEquals(
                expectedResult.getError().message(), actualResult.getError().message());
    }

    @Test
    void split_FromEntityIsNull_Negative() {
        CurrencyEntity USD = new CurrencyEntity();
        USD.setCode("USD");
        USD.setSymbol("$");

        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(1L).setToken("55555").setTitle("room_title");
        roomEntity.setCurrency(USD);

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(55555L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("test-description");

        when(roomService.findByToken("55555")).thenReturn(roomEntity);

        ResultK<CommandProcessed, CommandError> expectedResult = ResultK.Err(new CommandError(
                55555L, String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.getFromUsername())));

        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);

        assertNull(actualResult.getResult());
        assertEquals(expectedResult.getError().chatId(), actualResult.getError().chatId());
        assertEquals(
                expectedResult.getError().message(), actualResult.getError().message());
    }
}
