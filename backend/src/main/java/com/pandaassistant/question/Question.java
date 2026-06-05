package com.pandaassistant.question;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long subjectId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String stem;

    @Column(columnDefinition = "TEXT")
    private String answer;

    private String knowledgePoints;

    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String layoutJson;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}

