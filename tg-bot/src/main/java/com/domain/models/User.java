package com.domain.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "telegramid")
    private Long telegramId;

    @Column
    private String username;

    @Column
    private Integer score;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "registeredat", nullable = false)
    private LocalDateTime registeredAt;


    public User(Long telegramId, String username, LocalDateTime now) {
        this.telegramId = telegramId;
        this.username = username;
        this.score = 0;
        this.registeredAt = now;
        this.role = UserRole.USER;
    }

    public User() { }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}