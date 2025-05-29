package com.bolid.entity;

import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "users")
@Schema(description = "Модель пользователя Telegram")
public class User {
    @Id
    @Column(name = "telegramid")
    @Schema(description = "Telegram ID пользователя")
    private Long telegramId;

    @Column(name = "username")
    @Schema(description = "Имя пользователя в Telegram")
    private String username;

    @Column(name = "score")
    @Schema(description = "Количество баллов пользователя")
    private Integer points;

    @Column(name = "registeredat")
    @Schema(description = "Дата регистрации пользователя")
    private java.time.LocalDateTime registeredAt;

    @Column(name = "role")
    @Schema(description = "Роль пользователя")
    private String role;
} 