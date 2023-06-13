package ru.all_easy.push.telegram.commands.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.telegram.commands.service.model.PushCommandServiceError;
import ru.all_easy.push.telegram.commands.validators.model.PushCommandValidated;

@Primary
@Service
public class PushGroupCommandCacheService implements PushGroupCommandService {
    private final PushGroupCommandServiceImpl pushGroupCommandService;

    public PushGroupCommandCacheService(PushGroupCommandServiceImpl pushGroupCommandService) {
        this.pushGroupCommandService = pushGroupCommandService;
    }

    @Override
    public ResultK<String, PushCommandServiceError> push(PushCommandValidated validated) {
        return pushGroupCommandService.push(validated);
    }
}
