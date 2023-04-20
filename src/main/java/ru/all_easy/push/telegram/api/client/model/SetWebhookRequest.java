package ru.all_easy.push.telegram.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SetWebhookRequest(
        @JsonProperty("url")
        String url,

        @JsonProperty("drop_pending_updates")
        boolean dropPendingUpdates
) {
}
