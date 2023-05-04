package ru.all_easy.push.telegram.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record InlineKeyboard(
        @JsonProperty("inline_keyboard")
        List<List<InlineKeyboardButton>> inlineKeyboard
) {
}
