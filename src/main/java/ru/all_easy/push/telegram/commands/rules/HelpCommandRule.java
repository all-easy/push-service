// package ru.all_easy.push.telegram.commands.rules;
//
// import org.springframework.stereotype.Service;
// import ru.all_easy.push.common.ResultK;
// import ru.all_easy.push.telegram.api.controller.model.Update;
// import ru.all_easy.push.telegram.commands.Commands;
// import ru.all_easy.push.telegram.commands.rules.model.CommandError;
// import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
//
// @Service
// public class HelpCommandRule implements CommandRule {
//
//    @Override
//    public boolean apply(Update update) {
//        if (update.message() == null || update.message().text() == null) {
//            return false;
//        }
//        return update.message().text().contains(Commands.HELP.getCommand());
//    }
//
//    @Override
//    public ResultK<CommandProcessed, CommandError> process(Update update) {
//        String message =
//                "[Here you can find info about bot and how to use
// it](https://all-easy.notion.site/all-easy/Push-Money-Bot-c86ff025dd144f0aa67fee649e9fe538)";
//        return ResultK.Ok(new CommandProcessed(update.message().chat().id(), message));
//    }
// }
