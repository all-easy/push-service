package ru.all_easy.push.telegram.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.all_easy.push.common.client.model.ReplayMarkup;

public record InlineKeyboard(@JsonProperty("inline_keyboard") List<List<InlineKeyboardButton>> inlineKeyboard)
        implements ReplayMarkup {}
