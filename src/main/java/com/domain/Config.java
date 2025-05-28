package com.domain;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getBotUsername() {
        return dotenv.get("TG_NAME");
    }

    public static String getBotToken() {
        return dotenv.get("TG_TOKEN");
    }
}
