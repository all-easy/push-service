package ru.all_easy.push.telegram.api.client.model;

public record SetWebhookRequest(
    String url,
    boolean drop_pending_updates
) {
}
