package com.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;

public class EmUtill {
    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUrl == null || dbUser == null || dbPassword == null) {
            throw new IllegalStateException("Не заданы переменные окружения для подключения к БД");
        }

        return Persistence.createEntityManagerFactory("history", Map.of(
                "jakarta.persistence.jdbc.url", dbUrl,
                "jakarta.persistence.jdbc.user", dbUser,
                "jakarta.persistence.jdbc.password", dbPassword
        ));
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}