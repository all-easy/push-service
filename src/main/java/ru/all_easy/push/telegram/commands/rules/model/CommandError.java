package ru.all_easy.push.telegram.commands.rules.model;

public record CommandError(
        Long chatId,
        String message
) {
}
