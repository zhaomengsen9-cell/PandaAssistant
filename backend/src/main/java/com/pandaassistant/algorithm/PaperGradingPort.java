package com.pandaassistant.algorithm;

import java.util.List;

public interface PaperGradingPort {
    GradingDraft grade(Long subjectId, List<StudentAnswer> answers);
}

