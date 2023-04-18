package ru.all_easy.push.telegram.commands.rules;

import ru.all_easy.push.telegram.api.controller.model.Update;

public interface CommandRule {
    
    boolean apply(Update update);

    void process(Update update);

}
