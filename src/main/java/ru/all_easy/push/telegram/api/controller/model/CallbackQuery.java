package ru.all_easy.push.telegram.api.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CallbackQuery(
        @JsonProperty("id") String id,
        @JsonProperty("from") User from,
        @JsonProperty("message") Message message,
        @JsonProperty("inline_message_id") String inlineMessageId,
        @JsonProperty("chat_instance") String chatInstance,
        @JsonProperty("data") String data) {}
