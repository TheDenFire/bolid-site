package com.domain;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties not found");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    public static String getBotUsername() {
        return props.getProperty("telegram.bot.username");
    }

    public static String getBotToken() {
        return props.getProperty("telegram.bot.token");
    }
}
