package com.pandaassistant.analytics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "student_question_scores")
public class StudentQuestionScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentExamScoreId;
    private String questionNo;
    private BigDecimal score;
    private String knowledgePoint;
}

