package com.domain.controller.handlers;

import com.domain.models.User;
import com.domain.service.UserService;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class RatingHandler extends BaseHandler {

    private final UserService userService = new UserService();

    @Override
    public BotApiMethod<?> handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if ("/rating".equalsIgnoreCase(text)) {
                StringBuilder res = new StringBuilder("🏆 Вот 10 лучших игроков:\n\n");
                List<User> users = userService.Top10();

                for (User user : users) {
                    res.append("⭐️ ")
                            .append(user.getUsername())
                            .append(" — ")
                            .append(user.getScore()).append("/10\n");
                }

                return createMessage(chatId, res.toString());
            }
        }
        return null;
    }
}
