package ru.all_easy.push.common.client.model;

public record SetWebhookInfo(
    String url,
    boolean drop_pending_updates
) {
}
