package com.pandaassistant.algorithm;

public interface QuestionTaggingPort {
    QuestionTags tag(String stem, String answer);
}

