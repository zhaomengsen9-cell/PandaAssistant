package com.pandaassistant.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findTop20BySubjectIdAndDifficultyContainingIgnoreCase(Long subjectId, String difficulty);
}

