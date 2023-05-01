package ru.all_easy.push.telegram.commands.service;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.expense.repository.ExpenseEntity;
import ru.all_easy.push.expense.service.ExpenseService;
import ru.all_easy.push.expense.service.model.ExpenseInfo;
import ru.all_easy.push.helper.MathHelper;
import ru.all_easy.push.helper.NameAndAmountWithPercents;
import ru.all_easy.push.helper.PushHelper;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.room_user.repository.RoomUserEntity;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.MessageEntity;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.messages.AnswerMessageTemplate;

import java.math.BigDecimal;

@Service
public class PushGroupCommandServiceImpl implements PushGroupCommandService {
    private static final String TEXT_MENTION = "text_mention";

    private final ExpenseService expenseService;
    private final RoomService roomService;
    private final MathHelper mathHelper;
    private final PushHelper pushHelper;

    public PushGroupCommandServiceImpl(ExpenseService expenseService,
                                       RoomService roomService,
                                       MathHelper mathHelper,
                                       PushHelper pushHelper) {
        this.expenseService = expenseService;
        this.roomService = roomService;
        this.mathHelper = mathHelper;
        this.pushHelper = pushHelper;
    }

    @Override
    public SendMessageInfo getResult(Update update) {
        // TODO: refactor to parsing builder
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

        String fromUsername = update.message().from().username();
        if (fromUsername.equals(toUsername)) {
            return new SendMessageInfo(
                    chatId,
                    AnswerMessageTemplate.YOURSELF_PUSH.getMessage(),
                    ParseMode.MARKDOWN.getMode());
        }

        RoomEntity roomEntity = roomService.findByToken(String.valueOf(chatId));
        if (roomEntity == null) {
            String answerMessage = AnswerMessageTemplate.UNREGISTERED_ROOM.getMessage();
            return new SendMessageInfo(chatId, answerMessage, ParseMode.MARKDOWN.getMode());
        }

        RoomUserEntity fromEntity = findRoomUser(roomEntity, fromUsername);
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
            NameAndAmountWithPercents nameAndCalculatedAmount = pushHelper.getNameAndCalculatedAmount(
                    messageParts, calculatedAmount);
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

    private RoomUserEntity findRoomUser(RoomEntity room, String username) {
        return room.getUsers().stream()
                .filter(entity -> entity.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
