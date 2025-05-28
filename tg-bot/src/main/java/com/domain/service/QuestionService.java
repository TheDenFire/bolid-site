package com.domain.service;

import com.domain.dao.QuestionDAO;
import com.domain.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionDAO questionDAO;

    @Autowired
    public QuestionService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    public List<Question> getQuestions() {
        return questionDAO.getRandomQuestions(10);
    }
}
