package ru.all_easy.push.helper;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class GeneratorHelper {

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
