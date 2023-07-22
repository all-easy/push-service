// package ru.all_easy.push.telegram.commands.rules;
//
// import java.util.List;
// import org.springframework.stereotype.Service;
// import ru.all_easy.push.common.ResultK;
// import ru.all_easy.push.currency.repository.model.CurrencyEntity;
// import ru.all_easy.push.currency.service.CurrencyService;
// import ru.all_easy.push.currency.service.model.CurrencyInfo;
// import ru.all_easy.push.telegram.api.client.model.InlineKeyboard;
// import ru.all_easy.push.telegram.api.controller.model.Update;
// import ru.all_easy.push.telegram.api.tools.Keyboard;
// import ru.all_easy.push.telegram.commands.Commands;
// import ru.all_easy.push.telegram.commands.rules.model.CommandError;
// import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;
//
// @Service
// public class CurrencyCommandRule implements CommandRule {
//    private final CurrencyService currencyService;
//
//    public CurrencyCommandRule(CurrencyService currencyService) {
//        this.currencyService = currencyService;
//    }
//
//    @Override
//    public boolean apply(Update update) {
//        if (update.message() == null || update.message().text() == null) {
//            return false;
//        }
//        return update.message().text().contains(Commands.CURRENCY.getCommand());
//    }
//
//    @Override
//    public ResultK<CommandProcessed, CommandError> process(Update update) {
//        List<CurrencyInfo> currencies = currencyService.getAll();
//
//        Keyboard keyboard = new Keyboard(2);
//        for (CurrencyInfo currency : currencies) {
//            keyboard.addButton(currency.code() + " " + currency.symbol(), currency.code());
//        }
//
//        InlineKeyboard allButtons = new InlineKeyboard(keyboard.keyboard());
//
//        String message = "Please set up chat's currency";
//        CurrencyEntity currencyEntity = currencyService.getCurrencyByRoomId(
//                String.valueOf(update.message().chat().id()));
//        if (currencyEntity != null) {
//            message += ". Current is %s %s".formatted(currencyEntity.getSymbol(), currencyEntity.getCode());
//        }
//
//        return ResultK.Ok(new CommandProcessed(update.message().chat().id(), message, allButtons));
//    }
// }
