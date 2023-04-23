package ru.all_easy.push.telegram.commands.rules;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import ru.all_easy.push.common.MathHelper;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.MessageEntity;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.api.service.TelegramService;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

@Service
public class PushGroupCommandRule implements CommandRule {

    private static final String TEXT_MENTION = "text_mention";

    private final ExpenseService expenseService;
    private final RoomService roomService;
    private final MathHelper mathHelper;

    public PushGroupCommandRule(ExpenseService expenseService,
                                RoomService roomService,
                                MathHelper mathHelper) {
        this.expenseService = expenseService;
        this.roomService = roomService;
        this.mathHelper = mathHelper;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.PUSH.getCommand()) 
            && (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public SendMessageInfo process(Update update) {
        Long chatId = update.message().chat().id();
        String messageText = update.message().text();
        String[] messageParts = messageText.split(" ");
        SendMessageInfo validationMessage = validate(chatId, messageParts);
        if (validationMessage != null) {
            return validationMessage;
        }

        String toUsername = null;
	    for (MessageEntity entity : update.message().entities()) {
            if (TEXT_MENTION.equals(entity.type()) && !entity.user().username().isEmpty()) {
                toUsername = entity.user().username();
            }
	    }
	    
        if (messageParts[1].contains("@")) {
            toUsername = messageParts[1].replace("@", "");
	    }

        RoomEntity roomEntity = roomService.findByToken(String.valueOf(chatId));
        if (roomEntity == null) {
            String answerMessage = AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage();
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        }

        RoomUserEntity fromEntity = findRoomUser(roomEntity, update.message().from().username());
        if (fromEntity == null) {
            String answerMessage = String.format(
                AnswerMessageTemplate.UNADDED_USER.getMessage(), 
                update.message().from().username());
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        }

        RoomUserEntity toEntity = findRoomUser(roomEntity, toUsername);
        if (toEntity == null) {
            String answerMessage = String.format(AnswerMessageTemplate.UNADDED_USER.getMessage(), toUsername);
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        }
        
        try {
            BigDecimal calculatedAmount = mathHelper.calculate(messageParts[2]);
            String name = messageParts.length == 4 ? messageParts[3] : StringUtils.EMPTY;
            ExpenseInfo info = new ExpenseInfo(
                roomEntity.getToken(), 
                fromEntity.getUserUid(), 
                toEntity.getUserUid(),
                calculatedAmount, 
                name);
            
            ExpenseEntity result = expenseService.expense(info, roomEntity);
            String answerMessage = String.format(
                "Expense *%s* to user *%s* has been successfully added", 
                result.getAmount(), 
                result.getTo().getUsername());
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        } catch (IllegalArgumentException ex) {
            String answerErrorMessage = "Incorrect amount format 🤔";
            return new SendMessageInfo(chatId, answerErrorMessage, ParseMode.MARKDOWN.getMode());
        }
    }

    private SendMessageInfo validate(Long chatId, String[] messageParts) {
        if (messageParts.length < 3) {
            String answerMessage = "Incorrect format 🤔, try like this: /exp @to <amount>";
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        }

        return null;
    }

    private RoomUserEntity findRoomUser(RoomEntity room, String username) {
        return room.getUsers().stream()
            .filter(entity -> entity.getUser().getUsername().equals(username))
            .findFirst()
            .orElse(null);
    } 
    
}
