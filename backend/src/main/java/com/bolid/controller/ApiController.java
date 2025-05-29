package com.bolid.controller;

import com.bolid.entity.News;
import com.bolid.entity.User;
import com.bolid.repository.UserRepository;
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
    private final UserRepository telegramUserRepository;
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
                    schema = @Schema(implementation = User.class)
                )
            )
        }
    )
    public List<User> getTopUsers() {
        return telegramUserRepository.findTop5ByOrderByPointsDesc();
    }

    @GetMapping("/news")
    @Operation(
        summary = "Получить последние 3 новости",
        description = "Возвращает список из 3-х последних новостей, отсортированных по дате публикации",
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