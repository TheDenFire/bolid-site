package com.domain.dao;

import com.domain.dao.utill.EmUtill;
import com.domain.models.Option;
import com.domain.models.Question;
import jakarta.persistence.EntityManager;

import java.util.List;

public class OptionDAO {
    public void save(Option option) {
        try (EntityManager em = EmUtill.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(option);
            em.getTransaction().commit();
        }catch (Exception e){
            throw new RuntimeException("Ошибка при сохранении\n"+e);
        }
    }

    public List<Option> findByQuestionId(Long questionId) {
        try (EntityManager em = EmUtill.getEntityManager()) {
            return em.createQuery(
                            "SELECT o FROM Option o WHERE o.question.id = :questionId", Option.class)
                    .setParameter("questionId", questionId)
                    .getResultList();
        }
        catch (Exception e){
            throw new RuntimeException("Ошибка при поиске вариантов ответа"+e);
        }
    }
}
