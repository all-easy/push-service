package ru.all_easy.push.telegram.api.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Chat(
    
    @JsonProperty("id")
    Long id,

    @JsonProperty("type")
    String type,

    @JsonProperty("username")
    String username,

    @JsonProperty("title")
    String title

) {
}
