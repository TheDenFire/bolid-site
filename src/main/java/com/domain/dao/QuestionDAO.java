package com.domain.dao;

import com.domain.dao.utill.EmUtill;
import com.domain.models.Question;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestionDAO {
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

    public List<Question> getRandomQuestions(int limit) {
        try (EntityManager em = EmUtill.getEntityManager()) {
            // 1) Случайные id
            List<Long> ids = em.createQuery(
                            "SELECT q.id FROM Question q ORDER BY function('RANDOM')",
                            Long.class
                    )
                    .setMaxResults(limit)
                    .getResultList();

            if (ids.isEmpty()) {
                return Collections.emptyList();
            }

            // 2) Подтягиваем вопросы вместе с опциями
            List<Question> loaded = em.createQuery(
                            "SELECT DISTINCT q " +
                                    "FROM Question q " +
                                    "LEFT JOIN FETCH q.options " +
                                    "WHERE q.id IN :ids",
                            Question.class
                    )
                    .setParameter("ids", ids)
                    .getResultList();

            // 3) Восстанавливаем порядок, в котором шли ids
            Map<Long, Question> byId = loaded.stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));
            List<Question> ordered = new ArrayList<>(limit);
            for (Long id : ids) {
                Question q = byId.get(id);
                if (q != null) {
                    ordered.add(q);
                }
            }
            return ordered;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выборке случайных вопросов: " + e.getMessage(), e);
        }
    }


}
