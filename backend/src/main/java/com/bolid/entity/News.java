package com.bolid.entity;

import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "news")
@Schema(description = "Модель новости")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор новости")
    private int id;

    @Column(name = "title")
    @Schema(description = "Заголовок новости")
    private String title;

    @Column(name = "summary", length = 1000)
    @Schema(description = "Краткое описание новости")
    private String summary;

    @Column(name = "source_url")
    @Schema(description = "URL источника новости")
    private String sourceUrl;

    @Column(name = "image_url")
    @Schema(description = "URL картиночки")
    private String imageUrl;

    @Column(name = "original_published_at")
    @Schema(description = "Дата и время создания новости")
    private LocalDateTime publishedAt;
} 