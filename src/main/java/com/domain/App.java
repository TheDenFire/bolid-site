package com.domain;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.load();
            String telegramToken = dotenv.get("TG_TOKEN");
            String dbUrl = dotenv.get("DB_URL");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new HistoryBot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

