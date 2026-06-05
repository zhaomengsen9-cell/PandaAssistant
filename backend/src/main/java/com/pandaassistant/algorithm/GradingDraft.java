package com.pandaassistant.algorithm;

import java.math.BigDecimal;
import java.util.List;

public record GradingDraft(BigDecimal totalScore, List<QuestionScoreDraft> questionScores, String rubricSummary) {
}

