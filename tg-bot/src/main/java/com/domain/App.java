package com.domain;

import jakarta.persistence.EntityManager;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class App {
    public static void main(String[] args) {
        try {
            System.out.println("DB_URL: " + System.getenv("DB_URL"));
            System.out.println("DB_USER: " + System.getenv("DB_USER"));

            String token = System.getenv("TG_TOKEN");
            String deleteWebhookUrl = "https://api.telegram.org/bot" + token + "/deleteWebhook";
            try (var httpClient = HttpClients.createDefault()) {
                var request = new HttpPost(deleteWebhookUrl);
                var response = httpClient.execute(request);
                System.out.println("Webhook deletion response: " + EntityUtils.toString(response.getEntity()));
            }
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            HistoryBot bot = new HistoryBot();
            botsApi.registerBot(bot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

