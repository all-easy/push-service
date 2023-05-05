package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessage;
import ru.all_easy.push.common.client.model.SendMessageCurrencyInfo;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.currency.service.CurrencyService;
import ru.all_easy.push.currency.service.model.CurrencyInfo;
import ru.all_easy.push.telegram.api.client.model.InlineKeyboard;
import ru.all_easy.push.telegram.api.client.model.InlineKeyboardButton;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyCommandRule implements CommandRule {
    private final CurrencyService currencyService;

    public CurrencyCommandRule(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.CURRENCY.getCommand());
    }

    @Override
    public SendMessage process(Update update) {
        List<CurrencyInfo> currencies = currencyService.getAll();
        List<InlineKeyboardButton> buttonRow = new ArrayList<>();

        for (CurrencyInfo currency : currencies) {
            InlineKeyboardButton button = new InlineKeyboardButton(
                    currency.code() + " " + currency.symbol(), currency.code());
            buttonRow.add(button);
        }

        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        buttonRows.add(buttonRow);
        InlineKeyboard allButtons = new InlineKeyboard(buttonRows);

        String message = "Please set up chat's currency";
        CurrencyEntity currencyEntity = currencyService.getCurrencyByRoomId(update.message().chat().id());
        if (currencyEntity != null) {
            message += ". Current is %s %s".formatted(currencyEntity.getSymbol(), currencyEntity.getCode());
        }

        return new SendMessageCurrencyInfo(update.message().chat().id(), message, allButtons);
    }
}
