package ru.all_easy.push.telegram.commands.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.common.UnitTest;
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

/**
 * Unit testing for the main logic of the split command
 *
 * @see SplitCommandService
 */
class SplitCommandServiceTest extends UnitTest {

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
    void splitPositiveTest_ValidatedWithoutDescription() {

        // TEST DATA

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

        // STUBS

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

        // EXPECT

        String responseText =
                """
                Expense *33.33* $ USD to user *user_2* has been successfully added
                Expense *33.33* $ USD to user *user_3* has been successfully added
                """;
        ResultK<CommandProcessed, CommandError> expectedResult = ResultK.Ok(new CommandProcessed(11111L, responseText));

        // RESULT

        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);

        assertNull(actualResult.getError());
        assertEquals(11111L, actualResult.getResult().chatId());
        assertEquals(actualResult.getResult().message(), responseText);
        assertNull(actualResult.getResult().replayToId());
        assertNull(actualResult.getResult().replayMarkup());

        verify(roomService, times(1)).findByToken(eq("11111"));
        verify(expenseService, times(1)).expense(eq(expenseInfo1), eq(roomEntity));
        verify(expenseService, times(1)).expense(eq(expenseInfo2), eq(roomEntity));
    }

    @Test
    void splitPositiveTest_ValidatedWithDescription() {

        // TEST DATA

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

        // STUB

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

        // EXPECT

        String responseText =
                """
                Expense *33.33* $ USD to user *user_2* has been successfully added, description: test-description
                Expense *33.33* $ USD to user *user_3* has been successfully added, description: test-description
                """;

        // RESULT

        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);

        assertNull(actualResult.getError());
        assertEquals(11111L, actualResult.getResult().chatId());
        assertEquals(actualResult.getResult().message(), responseText);
        assertNull(actualResult.getResult().replayToId());
        assertNull(actualResult.getResult().replayMarkup());

        verify(roomService, times(1)).findByToken(eq("11111"));
        verify(expenseService, times(1)).expense(eq(expenseInfo1), eq(roomEntity));
        verify(expenseService, times(1)).expense(eq(expenseInfo2), eq(roomEntity));
    }

    @Test
    void splitNegativeTest_WithoutRoom() {

        // TEST DATA

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(55555L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("test-description");

        // STUB

        when(roomService.findByToken("55555")).thenReturn(null);

        // EXPECT

        ResultK<CommandProcessed, CommandError> expectedResult =
                ResultK.Err(new CommandError(55555L, AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage()));

        // RESULT

        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);

        assertNull(actualResult.getResult());
        assertEquals(expectedResult.getError().chatId(), actualResult.getError().chatId());
        assertEquals(
                expectedResult.getError().message(), actualResult.getError().message());

        verify(roomService, times(1)).findByToken(eq("55555"));
        verify(expenseService, times(0)).expense(any(ExpenseInfo.class), any(RoomEntity.class));
    }

    @Test
    void splitNegativeTest_WithoutCurrency() {

        // TEST DATA

        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setId(1L).setToken("55555").setTitle("room_title");

        SplitCommandValidated validated = new SplitCommandValidated();
        validated.setChatId(55555L);
        validated.setFromUsername("user_1");
        validated.setAmount(BigDecimal.valueOf(100.0));
        validated.setDescription("test-description");

        // STUB

        when(roomService.findByToken("55555")).thenReturn(roomEntity);

        // EXPECT

        ResultK<CommandProcessed, CommandError> expectedResult =
                ResultK.Err(new CommandError(55555L, AnswerMessageTemplate.UNSET_CURRENCY.getMessage()));

        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);

        // RESULT

        assertNull(actualResult.getResult());
        assertEquals(expectedResult.getError().chatId(), actualResult.getError().chatId());
        assertEquals(
                expectedResult.getError().message(), actualResult.getError().message());

        verify(roomService, times(1)).findByToken(eq("55555"));
        verify(expenseService, times(0)).expense(any(ExpenseInfo.class), any(RoomEntity.class));
    }

    @Test
    void splitNegativeTest_WithoutFromEntity() {

        // TEST DATA

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

        // STUB

        when(roomService.findByToken("55555")).thenReturn(roomEntity);

        // EXPECT

        ResultK<CommandProcessed, CommandError> expectedResult = ResultK.Err(new CommandError(
                55555L, String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), validated.getFromUsername())));

        ResultK<CommandProcessed, CommandError> actualResult = splitCommandService.split(validated);

        assertNull(actualResult.getResult());
        assertEquals(expectedResult.getError().chatId(), actualResult.getError().chatId());
        assertEquals(
                expectedResult.getError().message(), actualResult.getError().message());

        verify(roomService, times(1)).findByToken(eq("55555"));
        verify(expenseService, times(0)).expense(any(ExpenseInfo.class), any(RoomEntity.class));
    }
}
