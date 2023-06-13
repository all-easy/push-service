package ru.all_easy.push.telegram.api.tools;

import ru.all_easy.push.telegram.api.client.model.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {

    private final List<List<InlineKeyboardButton>> buttonRows;
    private final int buttonsPerRow;

    public Keyboard(int buttonsPerRow) {
        this.buttonsPerRow = buttonsPerRow;
        this.buttonRows = new ArrayList<>();
        this.buttonRows.add(new ArrayList<>());
    }

    public void addButton(String text, String callbackData) {
        List<InlineKeyboardButton> row = buttonRows.get(buttonRows.size() - 1);
        InlineKeyboardButton button = new InlineKeyboardButton(text, callbackData);
        row.add(button);
        if (row.size() == buttonsPerRow) {
            buttonRows.add(new ArrayList<>());
        }
    }

    public List<List<InlineKeyboardButton>> keyboard() {
        return buttonRows;
    }
}
