package ru.all_easy.push.telegram.api.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.all_easy.push.common.client.model.ReplayMarkup;

public record SendMessageRequest(
        @JsonProperty("chat_id") Long chatId,
        @JsonProperty("reply_to_message_id") @JsonInclude(JsonInclude.Include.NON_NULL) Integer replayToMessageId,
        @JsonProperty("text") String text,
        @JsonProperty("parse_mode") String parseMode,
        @JsonProperty("reply_markup") @JsonInclude(JsonInclude.Include.NON_NULL) ReplayMarkup replyMarkup) {}
