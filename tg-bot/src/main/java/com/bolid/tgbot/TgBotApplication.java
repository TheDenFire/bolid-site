package com.bolid.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bolid", "com.domain"})
@EntityScan(basePackages = {"com.domain.models"})
@EnableJpaRepositories(basePackages = {"com.domain.dao"})
public class TgBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgBotApplication.class, args);
    }

    @Component
    public class BotInitializer implements CommandLineRunner {
        
        @Autowired
        private HistoryBot historyBot;

        @Override
        public void run(String... args) throws Exception {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(historyBot);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
} 