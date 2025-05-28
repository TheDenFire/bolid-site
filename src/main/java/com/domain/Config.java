package com.domain;

public class Config {
    public static String getBotUsername() {
        return System.getenv("TG_TOKEN");
    }

    public static String getBotToken() {
        return System.getenv("TG_TOKEN");
    }
}
