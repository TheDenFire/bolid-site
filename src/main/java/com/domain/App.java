package com.domain;

import jakarta.persistence.EntityManager;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App {
    public static void main(String[] args) {
        EntityManager em = EmUtill.getEntityManager();
        em.close();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new HistoryBot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

