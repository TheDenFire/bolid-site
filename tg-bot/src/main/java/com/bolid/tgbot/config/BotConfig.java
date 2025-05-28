package com.bolid.tgbot.config;

import com.bolid.tgbot.HistoryBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Bean
    public HistoryBot historyBot() {
        return new HistoryBot(botUsername, botToken);
    }
} 