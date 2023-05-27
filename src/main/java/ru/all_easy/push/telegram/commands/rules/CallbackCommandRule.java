package ru.all_easy.push.telegram.commands.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.all_easy.push.common.ResultK;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.currency.service.CurrencyService;
import ru.all_easy.push.telegram.api.controller.model.Update;
import ru.all_easy.push.telegram.commands.rules.model.CommandError;
import ru.all_easy.push.telegram.commands.rules.model.CommandProcessed;

@Component
public class CallbackCommandRule implements CommandRule {

    private final CurrencyService currencyService;

    private static final Logger logger = LoggerFactory.getLogger(CallbackCommandRule.class);

    public CallbackCommandRule(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public boolean apply(Update update) {
        return update.callbackQuery() != null;
    }

    @Override
    public ResultK<CommandProcessed, CommandError> process(Update update) {
        logger.info("Callback received: {}", update);
        CurrencyEntity currency = currencyService.getCurrencyByCode(update.callbackQuery().data());
        currencyService.setCurrency(update.callbackQuery().message().chat().id(), currency);
        return ResultK.Ok(
                new CommandProcessed("Chat's currency is set to " + currency.getSymbol() + " " + currency.getCode()));
    }
}
