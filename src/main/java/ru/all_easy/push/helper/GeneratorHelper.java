package ru.all_easy.push.helper;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GeneratorHelper {

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
