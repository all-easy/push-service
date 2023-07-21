package ru.all_easy.push.telegram.api.controller;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import ru.all_easy.push.currency.repository.CurrencyRepository;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.repository.ExpenseRepository;
import ru.all_easy.push.helper.DateTimeHelper;
import ru.all_easy.push.room.repository.RoomRepository;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.room_user.repository.RoomUserRepository;
import ru.all_easy.push.user.repository.UserEntity;
import ru.all_easy.push.user.repository.UserRepository;

@TestComponent
public class TestData {
    private final DateTimeHelper dateTimeHelper = new DateTimeHelper();

    @Autowired
    UserRepository userRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomUserRepository roomUserRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    public void init() {
        expenseRepository.deleteAll();
        roomRepository.deleteAll();
        roomUserRepository.deleteAll();
        userRepository.deleteAll();
        createUsers();
        createRooms();
        createRoomToUser();
        createExpenses();
    }

    private void createUsers() {
        userRepository.save(new UserEntity().setId(1L).setUid("10001").setUsername("user_1"));
        userRepository.save(new UserEntity().setId(2L).setUid("10002").setUsername("user_2"));
        userRepository.save(new UserEntity().setId(3L).setUid("10003").setUsername("user_3"));
    }

    private void createRooms() {
        CurrencyEntity USD = currencyRepository.findByCode("USD");
        RoomEntity roomEntity1 = new RoomEntity().setId(1L).setToken("11111").setTitle("room_title_1");
        roomEntity1.setCurrency(USD);
        roomRepository.save(roomEntity1);

        CurrencyEntity EURO = currencyRepository.findByCode("EURO");
        RoomEntity roomEntity2 = new RoomEntity().setId(2L).setToken("22222").setTitle("room_title_2");
        roomEntity2.setCurrency(EURO);
        roomRepository.save(roomEntity2);
    }

    private void createRoomToUser() {
        roomUserRepository.save(
                new RoomUserEntity().setId(1L).setRoomToken("11111").setUserUid("10001"));
        roomUserRepository.save(
                new RoomUserEntity().setId(2L).setRoomToken("11111").setUserUid("10002"));
        roomUserRepository.save(
                new RoomUserEntity().setId(3L).setRoomToken("11111").setUserUid("10003"));
    }

    private void createExpenses() {
        UserEntity user1 = userRepository.findUserEntity("10001");
        UserEntity user2 = userRepository.findUserEntity("10002");
        UserEntity user3 = userRepository.findUserEntity("10003");
        UserEntity user4 = userRepository.findUserEntity("10004");
        RoomEntity room = roomRepository.findByToken("11111");
        CurrencyEntity USD = currencyRepository.findByCode("USD");
        expenseRepository.save(new ExpenseEntity()
                .setId(1L)
                .setName("description_1")
                .setFrom(user1)
                .setTo(user2)
                .setRoom(room)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(100))
                .setCurrency(USD));

        expenseRepository.save(new ExpenseEntity()
                .setId(2L)
                .setName("description_2")
                .setFrom(user1)
                .setTo(user3)
                .setRoom(room)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(50))
                .setCurrency(USD));

        expenseRepository.save(new ExpenseEntity()
                .setId(3L)
                .setName("description_3")
                .setFrom(user2)
                .setTo(user1)
                .setRoom(room)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(20))
                .setCurrency(USD));

        expenseRepository.save(new ExpenseEntity()
                .setId(4L)
                .setName("description_4")
                .setFrom(user2)
                .setTo(user3)
                .setRoom(room)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(40))
                .setCurrency(USD));

        expenseRepository.save(new ExpenseEntity()
                .setId(5L)
                .setName("description_5")
                .setFrom(user3)
                .setTo(user1)
                .setRoom(room)
                .setDateTime(dateTimeHelper.now())
                .setAmount(BigDecimal.valueOf(30))
                .setCurrency(USD));
    }
}
