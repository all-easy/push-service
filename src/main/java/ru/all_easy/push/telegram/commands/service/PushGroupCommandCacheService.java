package ru.all_easy.push.telegram.commands.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.controller.model.Update;

@Primary
@Service
public class PushGroupCommandCacheService implements PushGroupCommandService {
    @Autowired
    PushGroupCommandServiceImpl pushGroupCommandService;

    @Override
    // ? @Cacheable(?)
    public SendMessageInfo getResult(Update update) {
        return pushGroupCommandService.getResult(update);
    }
}
