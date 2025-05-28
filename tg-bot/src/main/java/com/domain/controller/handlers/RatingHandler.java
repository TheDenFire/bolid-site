package com.domain.controller.handlers;

import com.domain.models.User;
import com.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class RatingHandler extends BaseHandler {

    private final UserService userService;

    @Autowired
    public RatingHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public BotApiMethod<?> handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if ("/rating".equalsIgnoreCase(text)) {
                StringBuilder res = new StringBuilder("Вот 10 лучших игроков:\n\n");
                List<User> users = userService.Top10();

                for (User user : users) {
                    res.append(user.getUsername()).append(" ").append(user.getScore() + "/10 ").append("\n");
                }

                return createMessage(chatId, res.toString());
            }
        }
        return null;
    }
}
