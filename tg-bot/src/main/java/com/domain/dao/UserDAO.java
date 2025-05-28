package com.domain.dao;

import com.domain.dao.utill.EmUtill;
import com.domain.models.Option;
import com.domain.models.User;
import jakarta.persistence.EntityManager;

import java.util.List;

public class UserDAO {

    public void saveIfNotExists(User user) throws Exception {
        try (EntityManager em = EmUtill.getEntityManager()) {
            User existing = em.find(User.class, user.getTelegramId());
            if (existing == null) {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public void update(User user) {
        try (EntityManager em = EmUtill.getEntityManager()) {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось обновить пользователя", e);
        }
    }

    public User
    findByTelegramId(Long telegramId) throws Exception {
        try (EntityManager em = EmUtill.getEntityManager()) {
            return em.find(User.class, telegramId);
        }catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске пользователя по ID" + e);
        }
    }

    public User findByTelegramUsername(String username) {
        try (EntityManager em = EmUtill.getEntityManager()) {
            List<User> result = em.createQuery(
                            "SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)", User.class)
                    .setParameter("username", username)
                    .getResultList();

            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске пользователя по username @" + username, e);
        }
    }

    public List<User> top10Score() {
        try (EntityManager em = EmUtill.getEntityManager()) {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.score IS NOT NULL ORDER BY u.score DESC", User.class)
                    .setMaxResults(10)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при составлении рейтинга: " + e);
        }
    }

}
