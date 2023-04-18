package ru.all_easy.push.telegram.commands.rules;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfoDateTime;
import ru.all_easy.push.helper.DateTimeHelper;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.Commands;

@Service
public class HistoryCommandRule implements  CommandRule {

    private final ExpenseService expenseService;
    private final TelegramService telegramService;
    private final DateTimeHelper dateTimeHelper;

    private static final Integer MAX_HISTORY_LIMIT = 10;
    
    public HistoryCommandRule(ExpenseService expenseService, 
                              TelegramService telegramService, 
                              DateTimeHelper dateTimeHelper) {
        this.expenseService = expenseService;
        this.telegramService = telegramService;
        this.dateTimeHelper = dateTimeHelper;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.HISTORY.getCommand());
    }

    @Override
    public void process(Update update) {
        Long chatId = update.message().chat().id();

        List<ExpenseInfoDateTime> infoList = expenseService.findLimitRoomExpenses(String.valueOf(chatId), MAX_HISTORY_LIMIT);
        if (infoList.isEmpty()) {
            sendMessage(chatId, "History is empty");
            return;
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

        sendMessage(chatId, message.toString());
    }

    private void sendMessage(Long chatId, String message) {
        telegramService.sendMessage(new SendMessageInfo(chatId, message, ParseMode.MARKDOWN.getMode()));
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
