package ru.all_easy.push.telegram.commands.validators.model;

import ru.all_easy.push.common.Error;

public record ValidationError(Long chatId, String message) implements Error {}
