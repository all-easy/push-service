package ru.all_easy.push.telegram.commands.rules;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.service.ExpenseServiceHelper;
import ru.all_easy.push.expense.service.model.ExpenseInfoDateTime;
import ru.all_easy.push.helper.DateTimeHelper;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;

import java.util.List;

@Service
public class HistoryCommandRule implements  CommandRule {

    private final ExpenseServiceHelper expenseServiceHelper;
    private final DateTimeHelper dateTimeHelper;

    private static final Integer MAX_HISTORY_LIMIT = 10;
    
    public HistoryCommandRule(ExpenseServiceHelper expenseService,
                              DateTimeHelper dateTimeHelper) {
        this.expenseServiceHelper = expenseService;
        this.dateTimeHelper = dateTimeHelper;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.HISTORY.getCommand());
    }

    @Override
    public SendMessageInfo process(Update update) {
        Long chatId = update.message().chat().id();

        List<ExpenseInfoDateTime> infoList = expenseServiceHelper.findLimitRoomExpenses(String.valueOf(chatId), MAX_HISTORY_LIMIT);
        if (infoList.isEmpty()) {
            return new SendMessageInfo(chatId, "History is empty", ParseMode.MARKDOWN.getMode());
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

        return new SendMessageInfo(chatId, message.toString(), ParseMode.MARKDOWN.getMode());
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
