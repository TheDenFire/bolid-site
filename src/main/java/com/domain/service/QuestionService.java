package com.domain.service;

import com.domain.dao.QuestionDAO;
import com.domain.models.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class QuestionService {
    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);
    private final QuestionDAO questionDAO = new QuestionDAO();

    public List<Question> getQuestions() {
        try{
            return questionDAO.getRandomQuestions(10);
        }
        catch(Exception e){
            log.error(e.getMessage());
            return null;
        }

    }
}
