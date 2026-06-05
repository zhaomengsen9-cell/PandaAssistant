package com.pandaassistant.grading;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "grading_results")
public class GradingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subjectId;
    private String fileName;
    private BigDecimal totalScore;
    @Column(columnDefinition = "TEXT")
    private String resultJson;
    @Column(columnDefinition = "TEXT")
    private String teacherEditedJson;
    private Instant createdAt = Instant.now();
}

