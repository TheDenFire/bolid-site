package com.domain.dao;

import com.domain.dao.utill.EmUtill;
import com.domain.models.Question;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class QuestionDAO {

    @PersistenceContext
    private EntityManager em;

    public void save(Question question) {
        try (EntityManager em = EmUtill.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(question);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении вопроса\n" + e);
        }
    }

    public void update(Question question) {
        try (EntityManager em = EmUtill.getEntityManager()) {
            em.getTransaction().begin();
            em.merge(question);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка  обновлении вопроса\n" + e);
        }
    }

    public Question findById(String id) {
        try (EntityManager em = EmUtill.getEntityManager()) {
            return em.find(Question.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске вопроса\n" + e);
        }
    }

    public List<Question> getRandomQuestions(int count) {
        return em.createQuery(
                "SELECT q FROM Question q ORDER BY RANDOM()", Question.class)
                .setMaxResults(count)
                .getResultList();
    }
}
