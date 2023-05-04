package ru.all_easy.push.telegram.commands.rules;

import ru.all_easy.push.common.client.model.SendMessage;
import ru.all_easy.push.common.client.model.SendMessageInfo;
import ru.all_easy.push.telegram.api.controller.model.Update;

public interface CommandRule {
    
    boolean apply(Update update);

//    SendMessageInfo process(Update update);
    SendMessage process(Update update);

}
