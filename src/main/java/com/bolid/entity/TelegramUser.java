package com.bolid.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "telegram_users")
public class TelegramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @Column(name = "username")
    private String username;

    @Column(name = "points")
    private Integer points;
} 