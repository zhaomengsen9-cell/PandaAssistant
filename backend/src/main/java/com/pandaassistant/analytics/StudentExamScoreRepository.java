package com.pandaassistant.analytics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentExamScoreRepository extends JpaRepository<StudentExamScore, Long> {
    default List<StudentExamScore> findBySubjectIdAndScope(Long subjectId, String scopeType, String scopeName) {
        return findAll().stream()
                .filter(score -> score.getSubjectId().equals(subjectId))
                .filter(score -> "GRADE".equals(scopeType) ? scopeName.equals(score.getGradeName()) : scopeName.equals(score.getClassName()))
                .toList();
    }
}
