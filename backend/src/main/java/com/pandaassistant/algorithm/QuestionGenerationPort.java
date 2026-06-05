package com.pandaassistant.algorithm;

import com.pandaassistant.question.Question;

public interface QuestionGenerationPort {
    QuestionVariant generateVariant(Question sourceQuestion, String requirement);
}

