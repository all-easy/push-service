package ru.all_easy.push.telegram.api.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public TestData testData() {
        return new TestData();
    }
}
