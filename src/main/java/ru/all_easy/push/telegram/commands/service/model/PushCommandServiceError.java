package ru.all_easy.push.telegram.commands.service.model;

import ru.all_easy.push.common.Error;

public record PushCommandServiceError(Long chatId, String message) implements Error {}
