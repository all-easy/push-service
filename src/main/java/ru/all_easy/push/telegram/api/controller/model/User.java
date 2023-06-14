package ru.all_easy.push.telegram.api.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
        @JsonProperty("id") Long id,
        @JsonProperty("is_bot") Boolean isBot,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("username") String username) {}
