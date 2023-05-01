package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.service.ExpenseHelper;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.helper.FormatHelper;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ResultGroupCommandRule implements CommandRule {

//    private final ResultGroupCommandService resultGroupCommandService;
//
//    public ResultGroupCommandRule(ResultGroupCommandService resultGroupCommandService) {
//        this.resultGroupCommandService = resultGroupCommandService;
//    }

    private final RoomService roomService;
    private final ExpenseService expenseService;
    private final ExpenseHelper expenseHelper;
    private final FormatHelper formatHelper;

    public ResultGroupCommandRule(RoomService roomService,
                                  ExpenseService expenseService,
                                  ExpenseHelper expenseHelper, FormatHelper formatHelper) {
        this.roomService = roomService;
        this.expenseService = expenseService;
        this.expenseHelper = expenseHelper;
        this.formatHelper = formatHelper;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.RESULT.getCommand())
                && (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public SendMessageInfo process(Update update) {
//        return resultGroupCommandService.getResult(update);

        Long chatId = update.message().chat().id();
        String roomId = String.valueOf(chatId);

        RoomEntity roomEntity = roomService.findByToken(roomId);
        if (roomEntity == null) {
            return new SendMessageInfo(
                    update.message().chat().id(),
                    "Virtual is empty, please send /addme command 🙃",
                    ParseMode.MARKDOWN.getMode());
        }

//        Map<String, BigDecimal> optimize = expenseService.optimize(roomEntity);
        Map<String, BigDecimal> optimize = expenseHelper.optimize(roomEntity, chatId);
        String formattedMessage = formatHelper.formatResult(optimize);
        String message = formattedMessage.isEmpty() ? "No debts, chill for now \uD83D\uDE09" : formattedMessage;

        return new SendMessageInfo(
                update.message().chat().id(),
                message,
                ParseMode.MARKDOWN.getMode());
    }

}
