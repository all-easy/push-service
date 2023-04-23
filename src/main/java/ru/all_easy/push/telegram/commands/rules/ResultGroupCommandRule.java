package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.service.ExpenseService;
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
    private final ExpenseService expenseService;
    private final RoomService roomService;

    public ResultGroupCommandRule(ExpenseService expenseService,
                                  RoomService roomService) {
        this.expenseService = expenseService;
        this.roomService = roomService;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.RESULT.getCommand()) 
            && (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public SendMessageInfo process(Update update) {
        Long chatId = update.message().chat().id();
        String roomId = String.valueOf(chatId);
        
        RoomEntity roomEntity = roomService.findByToken(roomId);
        if (roomEntity == null) {
            String answerMessage = "Virtual is empty, please send /addme command 🙃";
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        }
        
        Map<String, BigDecimal> result = expenseService.optimize(roomEntity);
        StringBuilder stringBuilder = new StringBuilder();
        for (var set : result.entrySet()) {
            String[] participants = set.getKey().split(",");
            BigDecimal sum = set.getValue();
            stringBuilder.append(String.format("*%s* owes *%s* sum: *%s*\n", 
                participants[0], 
                participants[1],
                sum.doubleValue()));
        }

        return new SendMessageInfo(chatId, stringBuilder.toString(), ParseMode.MARKDOWN.getMode());
    }
    
}
