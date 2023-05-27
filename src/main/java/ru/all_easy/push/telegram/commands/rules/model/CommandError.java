package ru.all_easy.push.telegram.commands.rules.model;

public record CommandError(
        String message,
        Long chatId
) {
}
