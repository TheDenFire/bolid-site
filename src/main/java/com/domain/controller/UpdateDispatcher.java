package com.domain.controller;

import com.domain.controller.handlers.MainHandler;
import com.domain.controller.handlers.QuestionHandler;
import com.domain.controller.handlers.RatingHandler;
import com.domain.models.User;
import com.domain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class UpdateDispatcher {

    private static final UserService userService = new UserService();

    private static final MainHandler mainHandler = new MainHandler();

    private static final QuestionHandler questionHandler = new QuestionHandler();
    private static final RatingHandler ratingHandler = new RatingHandler();

    private static final Logger log = LoggerFactory.getLogger(UpdateDispatcher.class);


    public BotApiMethod<?> dispatch(Update update) {
        try {
            if (update.hasMessage()) {
                Long userId =  update.getMessage().getFrom().getId();
                String text = update.getMessage().getText();
                if (text != null) {
                    text = text.trim();

                    if (text.equalsIgnoreCase("/start")) {
                        User user = new User(update.getMessage().getChatId(),update.getMessage().getFrom().getUserName(), LocalDateTime.now());
                        userService.save(user);
                        return new SendMessage(userId.toString(),"""
                            👋 Привет! Добро пожаловать в *QuizBot*!
                    
                            Здесь ты можешь проверить свои знания, отвечая на вопросы в формате викторины.  
                            За каждый правильный ответ ты получаешь баллы, а в конце можешь увидеть себя в рейтинге лучших игроков.
                    
                            📌 Используй команды:
                            /quiz — начать викторину  
                            /rating — посмотреть топ-10 игроков  
                            /help — список всех доступных команд
                    
                            Удачи! 🍀
                            """);
                    } else if (text.equalsIgnoreCase("/quiz")) {
                        return questionHandler.handle(update);
                    } else if (text.equalsIgnoreCase("/rating")) {
                        return ratingHandler.handle(update);
                    } else if (text.equalsIgnoreCase("/help")) {
                        return new SendMessage(userId.toString(),
                                """
                                🤖 *Доступные команды:*
                        
                                /start — начать работу с ботом и зарегистрироваться  
                                /quiz — пройти викторину из 10 вопросов  
                                /rating — посмотреть рейтинг пользователей по набранным баллам  
                        
                                _Удачи и приятной игры!_
                                """);
                    }
                    else{
                        return new SendMessage(userId.toString(),"Неизвестная команда\nВведите /help чтобы показать справочное сообщение с командами");
                    }

                }
            } else if (update.hasCallbackQuery()) {
                String data = update.getCallbackQuery().getData();

                if (data != null) {
                    if (data.startsWith("start_quiz")) {
                        return questionHandler.handle(update);
                    }
                    else if (data.startsWith("answer")) {
                        return questionHandler.handle(update);
                    }else if (data.startsWith("next")) {
                        return questionHandler.handle(update);
                    }
                }
                return null;
            }
            return new SendMessage("0", "Неверный формат обновления.");
        } catch(Exception e){
            String chatId = "0";
            if (update.hasMessage()) {
                chatId = update.getMessage().getChatId().toString();
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            }
            log.info(e.getMessage());
            return new SendMessage(chatId, "Ошибка:");
        }

    }
}
