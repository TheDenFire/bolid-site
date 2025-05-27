package com.bolid.repository;

import com.bolid.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {
    
    @Query(value = "SELECT * FROM news ORDER BY original_published_at DESC LIMIT 3", nativeQuery = true)
    List<News> findLatestNews();
    
    @Query(value = "SELECT * FROM news WHERE original_published_at IS NOT NULL ORDER BY original_published_at DESC LIMIT 3", nativeQuery = true)
    List<News> findLatestOriginalNews();
    
    @Query(value = "SELECT * FROM news WHERE image_url IS NOT NULL ORDER BY published_at DESC LIMIT 3", nativeQuery = true)
    List<News> findLatestNewsWithImages();
} 