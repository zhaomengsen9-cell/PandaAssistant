package com.pandaassistant.algorithm;

import java.math.BigDecimal;
import java.util.List;

public record StudentScoreSnapshot(String studentNo, String studentName, BigDecimal totalScore, List<QuestionScoreDraft> scores) {
}

