package ru.all_easy.push.telegram.commands;

import java.util.List;

import org.springframework.stereotype.Service;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.rules.CommandRule;

@Service
public class CommandsContextService {
    
    private final List<CommandRule> commands;

    public CommandsContextService(List<CommandRule> commands) {
        this.commands = commands;
    }

    public void process(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return;
        }

        commands.stream()
            .filter(it -> it.apply(update))
            .findFirst()
            .ifPresent(it -> it.process(update));
    }

}
