package ru.all_easy.push.currency.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.all_easy.push.currency.repository.CurrencyRepository;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.currency.service.model.CurrencyInfo;
import ru.all_easy.push.room.service.RoomService;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final RoomService roomService;

    public CurrencyService(CurrencyRepository currencyRepository, RoomService roomService) {
        this.currencyRepository = currencyRepository;
        this.roomService = roomService;
    }

    public Flux<CurrencyInfo> getAll() {
        return currencyRepository.findAll().map(entity -> new CurrencyInfo(entity.getCode(), entity.getSymbol()));
    }

    public Mono<Void> updateCurrency(Long chatId, CurrencyEntity currency) {
        return roomService.setRoomCurrency(chatId, currency);
    }

    public Mono<String> getRoomCurrencyCode(String chatId) {
        return roomService.getRoomCurrency(chatId);
    }

    public Mono<CurrencyEntity> getCurrencyByCode(String currencyCode) {
        return currencyRepository.findByCode(currencyCode);
    }
}
