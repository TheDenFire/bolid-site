package com.bolid.dto;

import com.bolid.entity.News;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO для новости")
public class NewsDto {
    @Schema(description = "ID новости")
    private Integer id;

    @Schema(description = "Заголовок новости")
    private String title;

    @Schema(description = "Краткое описание новости")
    private String summary;

    @Schema(description = "URL источника новости")
    private String sourceUrl;

    @Schema(description = "URL изображения новости")
    private String imageUrl;

    @Schema(description = "Дата публикации")
    private LocalDateTime publishedAt;

    public static NewsDto fromEntity(News news) {
        NewsDto dto = new NewsDto();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setSummary(news.getSummary());
        dto.setSourceUrl(news.getSourceUrl());
        dto.setImageUrl(news.getImageUrl());
        dto.setPublishedAt(news.getOriginalPublishedAt());
        return dto;
    }
} 