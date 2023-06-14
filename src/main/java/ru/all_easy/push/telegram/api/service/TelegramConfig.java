package ru.all_easy.push.telegram.api.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramConfig(String hook, boolean dropPendingUpdates) {}
