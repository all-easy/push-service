package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.Commands;
import ru.all_easy.push.user.service.UserService;

@Service
public class SaveLink implements CommandRule {

    private final UserService userService;

    public SaveLink(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean apply(Update update) {
        return Commands.SAVE_LINK.getCommand().equals(update.message().replayToMessage().text());
    }

    @Override
    public void process(Update update) {
        System.out.println("LINK WAS REGISTERED");
        System.out.println(update.message().text());
    }
}
