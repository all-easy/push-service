package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
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
    private final ExpenseService expenseService;
    private final RoomService roomService;
    private final FormatHelper formatHelper;

    public ResultGroupCommandRule(ExpenseService expenseService,
                                  RoomService roomService,
                                  FormatHelper formatHelper) {
        this.expenseService = expenseService;
        this.roomService = roomService;
        this.formatHelper = formatHelper;
    }

    @Override
    public boolean apply(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }
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
            return ResultK.Ok(new CommandProcessed(answerMessage, chatId));
        }

        Map<String, BigDecimal> result = expenseService.optimize(roomEntity);
        String formattedMessage = formatHelper.formatResult(result);
        if (formattedMessage.isEmpty()) {
            String message = "No debts, chill for now \uD83D\uDE09";
            return ResultK.Ok(new CommandProcessed(message, chatId));
        }

        if (roomEntity.getCurrency() != null) {
            CurrencyEntity currencyEntity = roomEntity.getCurrency();
            StringBuilder stringBuilder = new StringBuilder(formattedMessage)
                    .append(" ")
                    .append(currencyEntity.getSymbol())
                    .append(" ")
                    .append(currencyEntity.getCode());
            formattedMessage = stringBuilder.toString();
        }

        return ResultK.Ok(new CommandProcessed(formattedMessage, chatId));
    }
    
}
