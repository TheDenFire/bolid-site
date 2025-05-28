package com.domain.dao;

import com.domain.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveIfNotExists(User user) {
        User existing = em.find(User.class, user.getTelegramId());
        if (existing == null) {
            em.persist(user);
        }
    }

    @Transactional
    public void update(User user) {
        em.merge(user);
    }

    public User findByTelegramId(Long telegramId) {
        return em.find(User.class, telegramId);
    }

    public User findByTelegramUsername(String username) {
        List<User> result = em.createQuery(
                "SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)", User.class)
                .setParameter("username", username)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public List<User> top10Score() {
        return em.createQuery(
                "SELECT u FROM User u ORDER BY u.score DESC", User.class)
                .setMaxResults(10)
                .getResultList();
    }
}
