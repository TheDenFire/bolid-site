package com.bolid.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "summary", length = 1000)
    private String summary;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 