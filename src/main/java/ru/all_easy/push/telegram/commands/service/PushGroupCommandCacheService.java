package ru.all_easy.push.telegram.commands.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.controller.model.Update;

@Primary
@Service
public class PushGroupCommandCacheService implements PushGroupCommandService {
    private final PushGroupCommandServiceImpl pushGroupCommandService;

    public PushGroupCommandCacheService(PushGroupCommandServiceImpl pushGroupCommandService) {
        this.pushGroupCommandService = pushGroupCommandService;
    }

    @Override
    @CacheEvict(value = "results", key = "#update.message().chat().id()")
    public SendMessageInfo getResult(Update update) {
        return pushGroupCommandService.getResult(update);
    }
}
