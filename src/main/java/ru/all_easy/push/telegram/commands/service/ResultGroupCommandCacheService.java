package ru.all_easy.push.telegram.commands.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.controller.model.Update;

@Service
@Primary
public class ResultGroupCommandCacheService implements ResultGroupCommandService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ResultGroupCommandServiceImpl resultGroupCommandService;

    public ResultGroupCommandCacheService(ResultGroupCommandServiceImpl resultGroupCommandService) {
        this.resultGroupCommandService = resultGroupCommandService;
    }

    @Override
//    @Cacheable(value = "results", key = "#update.message().chat().id()")
    public SendMessageInfo getResult(Update update) {
//        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
//        ops.set("my-key-1", "my-value-1");
//        ops.get("my-key-1");
//        redisTemplate.delete("my-key-1");

        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        hashOps.put("my-hash-key-1", "my-hash-key-1", "my-hash-value-1");
        hashOps.entries("my-hash-key-1");

        return resultGroupCommandService.getResult(update);
    }
}
