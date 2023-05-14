package ru.all_easy.push.telegram.commands.rules;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfoDateTime;
import ru.all_easy.push.helper.DateTimeHelper;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;

import java.util.List;

@Service
public class HistoryCommandRule implements  CommandRule {

    private final ExpenseService expenseService;
    private final DateTimeHelper dateTimeHelper;

    private static final Integer MAX_HISTORY_LIMIT = 10;
    
    public HistoryCommandRule(ExpenseService expenseService,
                              DateTimeHelper dateTimeHelper) {
        this.expenseService = expenseService;
        this.dateTimeHelper = dateTimeHelper;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.HISTORY.getCommand());
    }

    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        Long chatId = update.message().chat().id();

        List<ExpenseInfoDateTime> infoList = expenseService.findLimitRoomExpenses(String.valueOf(chatId), MAX_HISTORY_LIMIT);
        if (infoList.isEmpty()) {
            return ResultK.Ok(new CommandProcessed("History is empty"));
        }

        Integer virtualLimit = getLimit(update);
        virtualLimit = virtualLimit > infoList.size() ? infoList.size() : virtualLimit;
        StringBuilder message = new StringBuilder();
        for (var info : infoList.subList(infoList.size() - virtualLimit, infoList.size())) {
            message.append(String.format("*%s* +0, *%s*, *%s* -> *%s*, sum *%s*\n\n", 
                dateTimeHelper.toString(info.dateTime(), "dd-MM-yy HH:mm"),
                info.name(),
                info.fromUsername(), 
                info.toUsername(), 
                info.amount()));
        }

        return ResultK.Ok(new CommandProcessed(message.toString()));
    }

    private Integer getLimit(Update update) {
        String[] messageParts = update.message().text().split(" ");
        if (messageParts.length < 2) {
            return MAX_HISTORY_LIMIT;
        }

        String limitStr = messageParts[1];
        if (NumberUtils.isCreatable(limitStr)) {
            return Integer.valueOf(limitStr);
        }
        
        return MAX_HISTORY_LIMIT;
    }
    
}
