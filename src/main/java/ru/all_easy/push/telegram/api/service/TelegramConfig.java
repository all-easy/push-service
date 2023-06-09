package ru.all_easy.push.telegram.api.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.hook")
public record TelegramConfig(String hook, boolean dropPendingUpdates) {
}
