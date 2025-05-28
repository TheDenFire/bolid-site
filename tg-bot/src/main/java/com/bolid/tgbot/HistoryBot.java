package com.bolid.tgbot;

import com.domain.controller.UpdateDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HistoryBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(HistoryBot.class);
    
    private final String botUsername;
    private final String botToken;
    
    @Autowired
    private UpdateDispatcher updateDispatcher;

    public HistoryBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            BotApiMethod<?> response = updateDispatcher.dispatch(update);
            if (response != null) {
                execute(response);
            }
        } catch (TelegramApiException e) {
            log.error("Error sending response", e);
        }
    }
} 