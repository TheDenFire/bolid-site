package com.domain;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App {
    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.configure().load();
            String telegramToken = System.getenv("TG_TOKEN");
            String dbUrl = System.getenv("TG_TOKEN");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new HistoryBot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

