package com.domain.controller.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MainHandler extends BaseHandler {
    @Override
    public BotApiMethod<?> handle(Update update) {
        return null;
    }
}
