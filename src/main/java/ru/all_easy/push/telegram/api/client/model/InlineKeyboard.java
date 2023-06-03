package ru.all_easy.push.telegram.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.all_easy.push.common.client.model.ReplayMarkup;

import java.util.List;

public record InlineKeyboard(
        @JsonProperty("inline_keyboard")
        List<List<InlineKeyboardButton>> inlineKeyboard
) implements ReplayMarkup {
}
