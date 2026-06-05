package com.pandaassistant.analytics;

import com.pandaassistant.algorithm.LearningReportPort;
import com.pandaassistant.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final StudentExamScoreRepository scoreRepository;
    private final StudentQuestionScoreRepository questionScoreRepository;
    private final LearningReportRepository reportRepository;
    private final LearningReportPort learningReportPort;

    @PostMapping("/scores")
    public ApiResponse<StudentExamScore> saveScore(@Valid @RequestBody AnalyticsDtos.SaveScoreRequest request) {
        var score = new StudentExamScore();
        score.setSubjectId(request.subjectId());
        score.setExamName(request.examName());
        score.setClassName(request.className());
        score.setGradeName(request.gradeName());
        score.setStudentNo(request.studentNo());
        score.setStudentName(request.studentName());
        score.setTotalScore(request.totalScore());
        var saved = scoreRepository.save(score);
        if (request.questionScores() != null) {
            request.questionScores().forEach(input -> {
                var questionScore = new StudentQuestionScore();
                questionScore.setStudentExamScoreId(saved.getId());
                questionScore.setQuestionNo(input.questionNo());
                questionScore.setScore(input.score());
                questionScore.setKnowledgePoint(input.knowledgePoint());
                questionScoreRepository.save(questionScore);
            });
        }
        return ApiResponse.ok(saved);
    }

    @GetMapping("/scores")
    public ApiResponse<List<StudentExamScore>> listScores() {
        return ApiResponse.ok(scoreRepository.findAll());
    }

    @PostMapping("/reports")
    public ApiResponse<LearningReport> buildReport(@Valid @RequestBody AnalyticsDtos.ReportRequest request) {
        var report = new LearningReport();
        report.setSubjectId(request.subjectId());
        report.setScopeType(request.scopeType());
        report.setScopeName(request.scopeName());
        report.setReportText(learningReportPort.buildReport(request.scopeType(), request.scopeName(), request.subjectId(), List.of()));
        return ApiResponse.ok(reportRepository.save(report));
    }
}
