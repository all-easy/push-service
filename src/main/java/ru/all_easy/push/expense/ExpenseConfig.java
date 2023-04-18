package ru.all_easy.push.expense;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpenseConfig {

    @Bean
    public DoubleEvaluator doubleEvaluator() {
        return new DoubleEvaluator();
    }
}
