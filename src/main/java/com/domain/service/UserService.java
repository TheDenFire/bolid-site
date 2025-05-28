package com.domain.service;

import com.domain.dao.UserDAO;
import com.domain.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO = new UserDAO();

    public User findByUsername(String username) {
        try {
            return userDAO.findByTelegramUsername(username);

        }
        catch (Exception e){
            log.info(e.getMessage());
            return null;
        }
    }

    public User findById(Long id) {
        try {
            return userDAO.findByTelegramId(id);
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    public void save(User user) {
        try {
            userDAO.saveIfNotExists(user);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public List<User> Top10() {
        try {
            return userDAO.top10Score();
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    public void update(User user) {
        try {
            userDAO.update(user);
        }catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
