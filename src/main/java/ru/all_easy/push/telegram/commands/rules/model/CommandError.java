package ru.all_easy.push.telegram.commands.rules.model;

import ru.all_easy.push.common.Error;

public record CommandError(Long chatId, String message) implements Error {}
