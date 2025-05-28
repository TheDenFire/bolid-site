package com.bolid.service;

import com.bolid.entity.News;
import com.bolid.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    public List<News> getLatestNews() {
        return newsRepository.findLatestNews();
    }
} 