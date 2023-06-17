package ru.all_easy.push.telegram.api.supergroup;

import org.springframework.context.ApplicationEvent;
import ru.all_easy.push.telegram.api.controller.model.Update;

public class GroupToSuperGroupEvent extends ApplicationEvent {
    private Update update;

    public GroupToSuperGroupEvent(Object source, Update update) {
        super(source);
        this.update = update;
    }

    public Update getUpdate() {
        return update;
    }
}
