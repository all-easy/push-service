package ru.all_easy.push.helper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class DateTimeHelper {

    public String toString(LocalDateTime dateTime, String patten) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patten);
        return dateTime.format(formatter);
    }

    public String getCurrentTimestamp() {
        long nowTime = new Date().getTime();
        return Long.toString(nowTime);
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public LocalDateTime fromEpochSeconds(Long date) {
        return LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC);
    }
}
