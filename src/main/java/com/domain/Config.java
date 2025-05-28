package com.domain;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getBotUsername() {
        return System.getenv("TG_TOKEN");
    }

    public static String getBotToken() {
        return System.getenv("TG_TOKEN");
    }
}
