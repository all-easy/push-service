package ru.all_easy.push.user.controller;

public record LoginResponse(
        String username,
        String accessToken,
        String uid
) {
}
