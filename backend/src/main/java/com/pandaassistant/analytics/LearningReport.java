package com.pandaassistant.analytics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "learning_reports")
public class LearningReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subjectId;
    private String scopeType;
    private String scopeName;
    @Column(columnDefinition = "TEXT")
    private String reportText;
    private Instant createdAt = Instant.now();
}

