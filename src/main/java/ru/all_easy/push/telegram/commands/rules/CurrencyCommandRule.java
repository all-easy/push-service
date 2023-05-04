package ru.all_easy.push.telegram.commands.rules;

import com.sun.xml.bind.v2.TODO;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessage;
import ru.all_easy.push.common.client.model.SendMessageCurrencyInfo;
import ru.all_easy.push.telegram.api.client.model.InlineKeyboard;
import ru.all_easy.push.telegram.api.client.model.InlineKeyboardButton;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyCommandRule implements CommandRule {
    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.CURRENCY.getCommand());
    }

    @Override
    public SendMessage process(Update update) {
        // TODO: retrieve a list of the currencies from db
        InlineKeyboardButton buttonUSD = new InlineKeyboardButton("USD \u0024", "Your callback is USD");
        InlineKeyboardButton buttonGEL = new InlineKeyboardButton("GEL \u20be", "Your callback is GEL");
        InlineKeyboardButton buttonGBR = new InlineKeyboardButton("GBR \u00a3", "Your callback is GBR");
        InlineKeyboardButton buttonAMD = new InlineKeyboardButton("AMD \u058f", "Your callback is AMD");

        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        buttonRow.add(buttonUSD);
        buttonRow.add(buttonGEL);
        buttonRow.add(buttonGBR);
        buttonRow.add(buttonAMD);
        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        buttonRows.add(buttonRow);

        InlineKeyboard allButtons = new InlineKeyboard(buttonRows);
        return new SendMessageCurrencyInfo(update.message().chat().id(), "Please set up chat's currency", allButtons);
    }
}
