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
            NameAndCalculatedAmount nameAndCalculatedAmount = getNameAndCalculatedAmount(messageParts, calculatedAmount);
            String name = nameAndCalculatedAmount.name();
            ExpenseInfo info = new ExpenseInfo(
                roomEntity.getToken(), 
                fromEntity.getUserUid(), 
                toEntity.getUserUid(),
                nameAndCalculatedAmount.calculatedAmount(),
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
        if (messageParts.length < 3 || messageParts.length > 5) {
            String answerMessage =
                    "Incorrect format 🤔, try like this: /push @to <amount> <optional expense name> <optional amount%>";
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        }

        return null;
    }

    private Optional<Integer> validatePercentage(String value) {
        if (!value.contains("%") || value.isBlank() || value.equals(" ")) return Optional.empty();

        String valueStr = value.replace("%", "");
        for (int i = 0; i < valueStr.length(); i++) {
            if (!Character.isDigit(valueStr.charAt(i))) return Optional.empty();
        }

        int valueInt = Integer.parseInt(valueStr);
        if (valueInt <= 0 || valueInt > 100) return Optional.empty();

        return Optional.of(valueInt);
    }

    private NameAndCalculatedAmount getNameAndCalculatedAmount(String[] messageParts, BigDecimal calculatedAmount) {
        String name = StringUtils.EMPTY;

        // Case [0]/push [1]@username [2]math_expr [3]expNameOrPercentage?
        if (messageParts.length == 4) {
            Optional<Integer> percentageNumberOptional = validatePercentage(messageParts[3]);
            if (percentageNumberOptional.isPresent()) {
                int percentageNumber = percentageNumberOptional.get();
                calculatedAmount = BigDecimal.valueOf(calculatedAmount.doubleValue() * (100 + percentageNumber) / 100);
                name = StringUtils.EMPTY;
            } else {
                name = messageParts[3];
            }
        }

        // Case [0]/push [1]@username [2]math_expr [3]expName [4]Percentage
        if (messageParts.length == 5) {
            name = messageParts[3];
            Optional<Integer> percentageNumberOptional = validatePercentage(messageParts[4]);
            if (percentageNumberOptional.isPresent()) {
                int percentageNumber = percentageNumberOptional.get();
                calculatedAmount = BigDecimal.valueOf(calculatedAmount.doubleValue() * (100 + percentageNumber) / 100);
            } else {
                throw new IllegalArgumentException();
            }
        }

        return new NameAndCalculatedAmount(name, calculatedAmount);
    }

    private RoomUserEntity findRoomUser(RoomEntity room, String username) {
        return room.getUsers().stream()
            .filter(entity -> entity.getUser().getUsername().equals(username))
            .findFirst()
            .orElse(null);
    } 
    
}

record NameAndCalculatedAmount(String name, BigDecimal calculatedAmount) { }
