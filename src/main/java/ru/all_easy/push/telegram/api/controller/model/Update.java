package ru.all_easy.push.telegram.api.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Update(
    
    @JsonProperty("update_id")
    Long updateId,

    @JsonProperty("message")
    Message message,

    @JsonProperty("callback_query")
    CallbackQuery callbackQuery
) {
}
