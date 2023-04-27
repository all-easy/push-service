package ru.all_easy.push.telegram.commands.rules;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.helper.FormatHelper;
import ru.all_easy.push.telegram.api.ChatType;
import ru.all_easy.push.telegram.api.ParseMode;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.telegram.commands.service.ResultException;
import ru.all_easy.push.telegram.commands.service.ResultGroupCommandService;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ResultGroupCommandRule implements CommandRule {

    private final ResultGroupCommandService resultGroupCommandService;

    public ResultGroupCommandRule(ResultGroupCommandService resultGroupCommandService) {
        this.resultGroupCommandService = resultGroupCommandService;
    }

    @Override
    public boolean apply(Update update) {
        return update.message().text().contains(Commands.RESULT.getCommand()) 
            && (update.message().chat().type().equals(ChatType.SUPER_GROUP.getType())
                || update.message().chat().type().equals(ChatType.GROUP.getType()));
    }

    @Override
    public SendMessageInfo process(Update update) {
        return resultGroupCommandService.getResult(update);
    }

}
