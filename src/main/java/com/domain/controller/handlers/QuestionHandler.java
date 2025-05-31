package com.domain.controller.handlers;

import com.domain.controller.keyboard.AnswerKeyboard;
import com.domain.models.Question;
import com.domain.models.User;
import com.domain.service.QuestionService;
import com.domain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QuestionHandler extends BaseHandler{

    private static final QuestionService questionService = new QuestionService();
    private static final UserService userService = new UserService();
    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final Map<Long, LinkedList<Question>> session = new HashMap<>();


    @Override
    public BotApiMethod<?> handle(Update update) {
        String chatId = getChatId(update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            chatId = update.getMessage().getChatId().toString();

            if ("/quiz".equalsIgnoreCase(text)) {
                SendMessage welcome = new SendMessage();
                welcome.setChatId(chatId);
                welcome.setText("🎲 Начинаем викторину! Всего 10 вопросов. Удачи!");

                welcome.setReplyMarkup(AnswerKeyboard.start());



                return welcome;
            }
        }

        if (update.hasCallbackQuery()){
            String data = update.getCallbackQuery().getData();
            Long userId = update.getCallbackQuery().getFrom().getId();

            if (data.startsWith("start_quiz")) {
                LinkedList<Question> questions = new LinkedList<>(questionService.getQuestions());
                Question question = questions.pop();
                session.put(userId, questions);

                EditMessageText startQuiz = createEditMessage(
                        chatId,
                        update.getCallbackQuery().getMessage().getMessageId(),
                        "🎲 " + question.getText()  // добавили эмодзи перед вопросом
                );
                startQuiz.setReplyMarkup(AnswerKeyboard.build(question.getOptions()));
                return startQuiz;
            }

// Ответы
            if(data.startsWith("answer_")) {
                EditMessageText answer;
                if (data.endsWith("true")) {
                    answer = createEditMessage(
                            chatId,
                            update.getCallbackQuery().getMessage().getMessageId(),
                            "✅ Правильный ответ!!! 🎉\nИдём дальше 👉"
                    );
                    answer.setReplyMarkup(AnswerKeyboard.next());

                    User user = userService.findById(userId);
                    if(user.getScore()!=null){
                        log.info("user score is " + user.getScore());
                        user.setScore(user.getScore() + 1);
                    }

                    else {
                        user.setScore(1);
                    }
                    userService.update(user);
                } else {
                    answer = createEditMessage(
                            chatId,
                            update.getCallbackQuery().getMessage().getMessageId(),
                            "❌ Неверно 😞\nНо не унывай — ты справишься! 💪\nИдём дальше 👉"
                    );
                    answer.setReplyMarkup(AnswerKeyboard.next());
                }
                return answer;
            }

// Когда вопросы кончились
            if (data.startsWith("next")) {
                LinkedList<Question> questions = session.get(userId);
                if(!questions.isEmpty()){
                    Question question = questions.pop();
                    session.put(userId, questions);

                    EditMessageText startQuiz = createEditMessage(chatId,update.getCallbackQuery().getMessage().getMessageId(), "🎲 " + question.getText());
                    startQuiz.setReplyMarkup(AnswerKeyboard.build(question.getOptions()));
                    return startQuiz;
                } else {
                    User user = userService.findById(userId);
                    return createEditMessage(
                            chatId,
                            update.getCallbackQuery().getMessage().getMessageId(),
                            "🎉 Поздравляю! Ты ответил на " + user.getScore() + " из 10 вопросов!\n" +
                                    "📊 Чтобы увидеть общий рейтинг — введи /rating"
                    );
                }
            }

        }
        return null;
    }
}
