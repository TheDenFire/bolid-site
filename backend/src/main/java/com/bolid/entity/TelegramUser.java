package com.bolid.entity;

import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "telegram_users")
@Schema(description = "Модель пользователя Telegram")
public class TelegramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор пользователя")
    private int id;

    @Column(name = "telegram_id", unique = true)
    @Schema(description = "Telegram ID пользователя")
    private Long telegramId;

    @Column(name = "username")
    @Schema(description = "Имя пользователя в Telegram")
    private String username;

    @Column(name = "points")
    @Schema(description = "Количество баллов пользователя")
    private Integer points;
} 