package com.domain;

import com.domain.controller.UpdateDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HistoryBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(HistoryBot.class);
    public static HistoryBot INSTANCE;

    public HistoryBot() {
        INSTANCE = this;
    }

    private final UpdateDispatcher updateDispatcher = new UpdateDispatcher();

    @Override
    public void onUpdateReceived(Update update) {
        BotApiMethod<?> response = updateDispatcher.dispatch(update);
        if (response == null) return;
        try {
            execute(response);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке ответа", e);
        }
    }

    @Override
    public String getBotUsername() {
        return Config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return Config.getBotToken();
    }
}
