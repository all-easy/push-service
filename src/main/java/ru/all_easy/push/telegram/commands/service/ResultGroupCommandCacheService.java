package ru.all_easy.push.telegram.commands.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.controller.model.Update;

@Service
@Primary
public class ResultGroupCommandCacheService implements ResultGroupCommandService {

    private final ResultGroupCommandServiceImpl resultGroupCommandService;

    public ResultGroupCommandCacheService(ResultGroupCommandServiceImpl resultGroupCommandService) {
        this.resultGroupCommandService = resultGroupCommandService;
    }

    @Override
    public SendMessageInfo getResult(Update update) {
        return resultGroupCommandService.getResult(update);
    }
}
