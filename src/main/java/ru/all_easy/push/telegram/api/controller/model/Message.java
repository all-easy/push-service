package ru.all_easy.push.telegram.api.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
        @JsonProperty("message_id") Integer messageId,
        @JsonProperty("from") User from,
        @JsonProperty("chat") Chat chat,
        @JsonProperty("date") Long date,
        @JsonProperty("text") String text,
        @JsonProperty("reply_to_message") Message replayToMessage,
        @JsonProperty("entities") List<MessageEntity> entities,
        @JsonProperty("migrate_to_chat_id") Integer migrateToChatId,
        @JsonProperty("migrate_from_chat_id") Integer migrateFromChatId) {
    public Message(
            Integer messageId,
            User from,
            Chat chat,
            Long date,
            String text,
            Message replayToMessage,
            List<MessageEntity> entities) {
        this(messageId, from, chat, date, text, replayToMessage, entities, null, null);
    }
}
