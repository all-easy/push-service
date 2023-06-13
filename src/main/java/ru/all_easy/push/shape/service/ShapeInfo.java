package ru.all_easy.push.shape.service;

import ru.all_easy.push.shape.repository.Shape;

public record ShapeInfo(Shape shape, String uid, String roomToken) {}
