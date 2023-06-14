package ru.all_easy.push.telegram.commands.rules;

import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;

public interface CommandRule {

    boolean apply(Update update);

    ResultK<CommandProcessed, CommandError> process(Update update);
}
