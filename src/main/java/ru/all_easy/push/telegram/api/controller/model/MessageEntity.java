package ru.all_easy.push.telegram.api.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageEntity(
        @JsonProperty("type") String type, @JsonProperty("offset") Integer offset, @JsonProperty("user") User user) {}
