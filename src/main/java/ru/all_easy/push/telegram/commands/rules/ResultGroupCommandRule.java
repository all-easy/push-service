package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.helper.FormatHelper;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ResultGroupCommandRule implements CommandRule {
    private final RoomService roomService;
    private final ExpenseService expenseService;
    private final FormatHelper formatHelper;

    public ResultGroupCommandRule(RoomService roomService,
                                  ExpenseService expenseService,
                                  FormatHelper formatHelper) {
        this.roomService = roomService;
        this.expenseService = expenseService;
        this.formatHelper = formatHelper;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.RESULT.getCommand())
                && (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        Long chatId = update.message().chat().id();
        String roomId = String.valueOf(chatId);

        RoomEntity roomEntity = roomService.findByToken(roomId);
        if (roomEntity == null) {
            String answerMessage = "Virtual is empty, please send /addme command 🙃";
            return ResultK.Ok(new CommandProcessed(answerMessage));
        }

        Map<String, BigDecimal> optimize = expenseService.optimize(roomEntity);
        String formattedMessage = formatHelper.formatResult(optimize);
        String message = formattedMessage.isEmpty() ? "No debts, chill for now \uD83D\uDE09" : formattedMessage;
        return ResultK.Ok(new CommandProcessed(message));
    }

}
