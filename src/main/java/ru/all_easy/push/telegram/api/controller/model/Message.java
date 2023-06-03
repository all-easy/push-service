package ru.all_easy.push.telegram.api.controller.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
    
    @JsonProperty("message_id")
    Long messageId,

    @JsonProperty("from")
    User from,

    @JsonProperty("chat")
    Chat chat,

    @JsonProperty("date")
    Long date,

    @JsonProperty("text")
    String text,

    @JsonProperty("reply_to_message")
    Message replayToMessage,

    @JsonProperty("entities")
    List<MessageEntity> entities
) {
}
