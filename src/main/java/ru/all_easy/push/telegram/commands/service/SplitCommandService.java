package ru.all_easy.push.telegram.commands.service;

import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
import ru.all_easy.push.telegram.commands.validators.model.SplitCommandValidated;

public class SplitCommandService {
    public ResultK<CommandProcessed, CommandError> split(SplitCommandValidated validated) {
        return null;
    }
}
