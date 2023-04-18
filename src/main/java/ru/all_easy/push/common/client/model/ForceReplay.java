package ru.all_easy.push.common.client.model;

public record ForceReplay(
        Boolean forceReply,

        String inputFieldPlaceholder,

        Boolean selective
) {
}
