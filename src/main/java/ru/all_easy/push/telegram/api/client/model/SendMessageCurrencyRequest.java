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
    public SendMessageCurrencyRequest(@JsonProperty("chat_id") Long chatId,
                                      @JsonProperty("text") String text,
                                      @JsonProperty("reply_markup") InlineKeyboard replyMarkup) {
        this.chatId = chatId;
        this.text = text;
        this.replyMarkup = replyMarkup;
    }

    public SendMessageCurrencyRequest(@JsonProperty("chat_id") Long chatId,
                                      @JsonProperty("reply_markup") InlineKeyboard replyMarkup) {
        this(chatId, "Please set up chat's currency", replyMarkup);
    }
}
