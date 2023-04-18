package ru.all_easy.push.telegram.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ForceReply(
        @JsonProperty("force_reply")
        Boolean forceReply,

        @JsonProperty("input_field_placeholder")
        String inputFieldPlaceholder,

        @JsonProperty("selective")
        Boolean selective
) {
}
