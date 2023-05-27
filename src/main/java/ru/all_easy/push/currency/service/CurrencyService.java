package ru.all_easy.push.currency.service;

import org.springframework.stereotype.Service;
import ru.all_easy.push.currency.repository.CurrencyRepository;
import ru.all_easy.push.currency.repository.model.CurrencyEntity;
import ru.all_easy.push.currency.service.model.CurrencyInfo;
import ru.all_easy.push.room.repository.RoomRepository;
import ru.all_easy.push.room.repository.model.RoomEntity;
import ru.all_easy.push.room.service.RoomService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final RoomService roomService;

    public CurrencyService(CurrencyRepository currencyRepository, RoomService roomService) {
        this.currencyRepository = currencyRepository;
        this.roomService = roomService;
    }

    public List<CurrencyInfo> getAll() {
        return currencyRepository.findAll().stream()
                .map(entity -> new CurrencyInfo(entity.getCode(), entity.getSymbol()))
                .collect(Collectors.toList());
    }

    public void setCurrency(Long chatId, String currencyCode) {
        RoomEntity room = roomService.findRoomByToken(String.valueOf(chatId));
        CurrencyEntity currency = currencyRepository.findByCode(currencyCode);
        roomService.setRoomCurrency(room, currency);
    }

    public CurrencyEntity getCurrencyByRoomId(Long chatId) {
        RoomEntity roomEntity = roomService.findByToken(String.valueOf(chatId));
        return roomEntity == null ? null : roomEntity.getCurrency();
    }

    public String getCurrencySymbolAndCode(String currencyCode) {
        CurrencyEntity currencyEntity = currencyRepository.findByCode(currencyCode);
        return currencyEntity.getSymbol() + " " + currencyEntity.getCode();
    }

}
