package ru.all_easy.push.telegram.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendMessageCurrencyRequest(
        @JsonProperty("chat_id")
        Long chatId,

        @JsonProperty("text")
        String text,

        @JsonProperty("reply_markup")
        InlineKeyboard replyMarkup
) {
}
