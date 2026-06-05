package com.pandaassistant.algorithm;

import com.pandaassistant.question.Question;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class StubAlgorithmAdapter implements DocumentUnderstandingPort, QuestionTaggingPort,
        QuestionGenerationPort, VectorKnowledgeBasePort, PaperGradingPort, LearningReportPort {
    @Override
    public List<RecognizedQuestion> recognizeQuestions(String fileName, byte[] content) {
        return List.of(new RecognizedQuestion("识别占位题干：" + fileName, "识别占位答案", "{}"));
    }

    @Override
    public List<StudentAnswer> recognizeStudentAnswers(String fileName, byte[] content) {
        return List.of(new StudentAnswer("1", "学生答案占位：" + fileName, "{}"));
    }

    @Override
    public QuestionTags tag(String stem, String answer) {
        return new QuestionTags("待确认科目", List.of("待确认知识点"), "MEDIUM");
    }

    @Override
    public QuestionVariant generateVariant(Question sourceQuestion, String requirement) {
        return new QuestionVariant(sourceQuestion.getStem() + "（变式占位）", sourceQuestion.getAnswer(), requirement);
    }

    @Override
    public void ingestMaterial(Long subjectId, String fileName, byte[] content) {
    }

    @Override
    public List<String> retrieveEvidence(Long subjectId, String query) {
        return List.of("RAG 证据占位：" + query);
    }

    @Override
    public GradingDraft grade(Long subjectId, List<StudentAnswer> answers) {
        return new GradingDraft(BigDecimal.ZERO, List.of(), "评阅占位结果，可编辑后保存");
    }

    @Override
    public String buildReport(String scopeType, String scopeName, Long subjectId, List<StudentScoreSnapshot> scores) {
        return "学情分析报告占位：后续接入大模型生成固定格式报告。";
    }
}

