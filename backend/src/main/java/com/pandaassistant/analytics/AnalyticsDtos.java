package com.pandaassistant.analytics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class AnalyticsDtos {
    public record SaveScoreRequest(@NotNull Long subjectId, @NotBlank String examName, String className,
                                   String gradeName, @NotBlank String studentNo, @NotBlank String studentName,
                                   BigDecimal totalScore, List<QuestionScoreInput> questionScores) {
    }

    public record QuestionScoreInput(String questionNo, BigDecimal score, String knowledgePoint) {
    }

    public record ReportRequest(@NotNull Long subjectId, @NotBlank String scopeType, @NotBlank String scopeName) {
    }
}

