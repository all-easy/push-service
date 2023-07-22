// package ru.all_easy.push.telegram.commands.rules;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;
// import ru.all_easy.push.common.ResultK;
// import ru.all_easy.push.room.service.RoomService;
// import ru.all_easy.push.telegram.api.controller.model.Update;
// import ru.all_easy.push.telegram.commands.rules.model.CommandError;
// import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
//
// @Service
// public class AutoMigrationRule implements CommandRule {
//    private final RoomService roomService;
//
//    private static final Logger logger = LoggerFactory.getLogger(AutoMigrationRule.class);
//
//    public AutoMigrationRule(RoomService roomService) {
//        this.roomService = roomService;
//    }
//
//    @Override
//    public boolean apply(Update update) {
//        if (update.message() == null) return false;
//        return update.message().migrateFromChatId() != null;
//    }
//
//    @Override
//    public ResultK<CommandProcessed, CommandError> process(Update update) {
//        final String oldToken = String.valueOf(update.message().migrateFromChatId());
//        final String newToken = String.valueOf(update.message().chat().id());
//
//        try {
//            roomService.autoMigrateToSupergroup(oldToken, newToken);
//        } catch (Exception e) {
//            logger.error("An error occurred while upgrading to supergroup: {}", e.getMessage());
//            return ResultK.Err(new CommandError(
//                    update.message().chat().id(), "Something went wrong while upgrading to supergroup"));
//        }
//
//        return ResultK.Ok(new CommandProcessed(update.message().chat().id(), "Chat has been upgraded to supergroup"));
//    }
// }
