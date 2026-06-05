package com.pandaassistant.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class QuestionDtos {
    public record SaveQuestionRequest(Long subjectId, @NotBlank String stem, String answer,
                                      List<String> knowledgePoints, String difficulty, String layoutJson) {
    }

    public record RecommendRequest(@NotNull Long subjectId, List<String> knowledgePoints,
                                   String difficulty, boolean variant, String requirement) {
    }
}

