package com.domain.controller.keyboard;

import com.domain.models.Option;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class AnswerKeyboard {
    public static InlineKeyboardMarkup build(List<Option> options) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Option option : options) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(option.getText());

            String callback = option.isCorrect() ? "answer_true" : "answer_false";
            button.setCallbackData(callback);

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rows.add(row);
        }

        markup.setKeyboard(rows);
        return markup;
    }

    public static InlineKeyboardMarkup start() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText("🚀 Начнём!");
        button.setCallbackData("start_quiz");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }

    public static InlineKeyboardMarkup next() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText("👉 Следующий вопрос?");
        button.setCallbackData("next");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }
}
