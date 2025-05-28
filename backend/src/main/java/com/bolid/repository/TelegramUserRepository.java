package com.bolid.repository;

import com.bolid.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    @Query("SELECT t FROM TelegramUser t ORDER BY t.points DESC LIMIT 5")
    List<TelegramUser> findTop5ByOrderByPointsDesc();
} 