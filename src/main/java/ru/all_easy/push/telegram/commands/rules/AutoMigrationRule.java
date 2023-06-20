package ru.all_easy.push.telegram.commands.rules;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.currency.service.CurrencyService;
import ru.all_easy.push.room.service.RoomService;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;

@Service
public class AutoMigrationRule implements CommandRule {
    private final RoomService roomService;
    private final CurrencyService currencyService;

    public AutoMigrationRule(RoomService roomService, CurrencyService currencyService) {
        this.roomService = roomService;
        this.currencyService = currencyService;
    }

    @Override
    public boolean apply(Update update) {
        if (update.message() == null) return false;
        return update.message().migrateFromChatId() != null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        final String oldToken = String.valueOf(update.message().migrateFromChatId());
        final String newToken = String.valueOf(update.message().chat().id());

        try {
            roomService.autoMigrateToSupergroup(oldToken, newToken);
        } catch (Exception e) {
            return ResultK.Err(new CommandError(update.message().chat().id(), e.getMessage()));
        }

        return ResultK.Ok(new CommandProcessed(
                update.message().chat().id(), "Moved to supergroup, from " + oldToken + " to " + newToken));
    }
}
