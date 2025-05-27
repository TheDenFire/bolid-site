package com.bolid.controller;

import com.bolid.entity.News;
import com.bolid.entity.TelegramUser;
import com.bolid.repository.TelegramUserRepository;
import com.bolid.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Bolid API", description = "API для получения данных о пользователях и новостях")
public class ApiController {
    private final TelegramUserRepository telegramUserRepository;
    private final NewsService newsService;

    @GetMapping("/top-users")
    @Operation(
        summary = "Получить топ-5 пользователей",
        description = "Возвращает список из 5 пользователей с наивысшими баллами",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Успешное получение списка пользователей",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TelegramUser.class))
                )
            )
        }
    )
    public List<TelegramUser> getTopUsers() {
        return telegramUserRepository.findTop5ByOrderByPointsDesc();
    }

    @GetMapping("/news")
    @Operation(
        summary = "Получить последние новости",
        description = "Возвращает список последних новостей с заголовками и краткими описаниями",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Успешное получение списка новостей",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = News.class))
                )
            )
        }
    )
    public List<News> getNews() {
        return newsService.getLatestNews();
    }
} 