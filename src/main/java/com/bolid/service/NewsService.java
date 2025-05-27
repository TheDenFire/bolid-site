package com.bolid.service;

import com.bolid.entity.News;
import com.bolid.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    
    private final NewsRepository newsRepository;
    
    @Autowired
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    
    public List<News> getLatestNews() {
        return newsRepository.findLatestNews();
    }
    
    public List<News> getLatestOriginalNews() {
        return newsRepository.findLatestOriginalNews();
    }
    
    public List<News> getLatestNewsWithImages() {
        return newsRepository.findLatestNewsWithImages();
    }
} 