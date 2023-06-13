package ru.all_easy.push.user.controller;

public record RegisterRequest(
        String username,
        String password
) {
}
