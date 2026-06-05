package com.pandaassistant.algorithm;

import java.util.List;

public interface LearningReportPort {
    String buildReport(String scopeType, String scopeName, Long subjectId, List<StudentScoreSnapshot> scores);
}

