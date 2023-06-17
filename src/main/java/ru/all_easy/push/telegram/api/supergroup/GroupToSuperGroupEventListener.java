package ru.all_easy.push.telegram.api.supergroup;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class GroupToSuperGroupEventListener implements ApplicationListener<GroupToSuperGroupEvent> {
    @Override
    public void onApplicationEvent(GroupToSuperGroupEvent event) {
        // logic
        // change token in room table, then in expense table, then in room_t_user
        // send message SendMessageInfo(Long chatId, String text, String parseMode)
    }
}
