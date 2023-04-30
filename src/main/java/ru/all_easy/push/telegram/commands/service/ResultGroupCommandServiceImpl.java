package ru.all_easy.push.telegram.commands.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.helper.FormatHelper;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ResultGroupCommandServiceImpl implements ResultGroupCommandService {

    private final RoomService roomService;
    private final ExpenseService expenseService;
    private final FormatHelper formatHelper;

    public ResultGroupCommandServiceImpl(RoomService roomService,
                                         ExpenseService expenseService,
                                         FormatHelper formatHelper) {
        this.roomService = roomService;
        this.expenseService = expenseService;
        this.formatHelper = formatHelper;
    }

    @Override
    public SendMessageInfo getResult(Update update) {
        Long chatId = update.message().chat().id();
        String roomId = String.valueOf(chatId);

        RoomEntity roomEntity = roomService.findByToken(roomId);
        if (roomEntity == null) {
            return new SendMessageInfo(
                    update.message().chat().id(),
                    "Virtual is empty, please send /addme command 🙃",
                    ParseMode.MARKDOWN.getMode());
        }

        Map<String, BigDecimal> optimize = expenseService.optimize(roomEntity);
        String formattedMessage = formatHelper.formatResult(optimize);
        String message = formattedMessage.isEmpty() ? "No debts, chill for now \uD83D\uDE09" : formattedMessage;

        return new SendMessageInfo(
                update.message().chat().id(),
                message,
                ParseMode.MARKDOWN.getMode());
    }

}
