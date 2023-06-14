package ru.all_easy.push.telegram.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InlineKeyboardButton(
        @JsonProperty("text") String text, @JsonProperty("callback_data") String callbackData) {}
