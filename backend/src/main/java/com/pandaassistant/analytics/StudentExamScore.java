package com.pandaassistant.analytics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "student_exam_scores")
public class StudentExamScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subjectId;
    private String examName;
    private String className;
    private String gradeName;
    private String studentNo;
    private String studentName;
    private BigDecimal totalScore;
}

