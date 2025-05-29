package com.bolid.repository;

import com.bolid.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT t FROM User t ORDER BY t.points DESC LIMIT 5")
    List<User> findTop5ByOrderByPointsDesc();
} 