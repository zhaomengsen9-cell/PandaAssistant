package com.pandaassistant.question;

import com.pandaassistant.algorithm.DocumentUnderstandingPort;
import com.pandaassistant.algorithm.QuestionGenerationPort;
import com.pandaassistant.algorithm.QuestionTaggingPort;
import com.pandaassistant.algorithm.RecognizedQuestion;
import com.pandaassistant.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionRepository questionRepository;
    private final DocumentUnderstandingPort documentUnderstandingPort;
    private final QuestionTaggingPort questionTaggingPort;
    private final QuestionGenerationPort questionGenerationPort;

    @PostMapping("/imports")
    public ApiResponse<List<RecognizedQuestion>> importQuestionBank(@RequestPart MultipartFile file) throws IOException {
        return ApiResponse.ok(documentUnderstandingPort.recognizeQuestions(file.getOriginalFilename(), file.getBytes()));
    }

    @PostMapping
    public ApiResponse<Question> saveQuestion(@Valid @RequestBody QuestionDtos.SaveQuestionRequest request) {
        var tags = questionTaggingPort.tag(request.stem(), request.answer());
        var question = new Question();
        question.setSubjectId(request.subjectId());
        question.setStem(request.stem());
        question.setAnswer(request.answer());
        question.setDifficulty(request.difficulty() == null ? tags.difficulty() : request.difficulty());
        question.setKnowledgePoints(String.join(",", request.knowledgePoints() == null ? tags.knowledgePoints() : request.knowledgePoints()));
        question.setLayoutJson(request.layoutJson());
        return ApiResponse.ok(questionRepository.save(question));
    }

    @GetMapping
    public ApiResponse<List<Question>> listQuestions() {
        return ApiResponse.ok(questionRepository.findAll());
    }

    @PostMapping("/recommendations")
    public ApiResponse<List<?>> recommend(@Valid @RequestBody QuestionDtos.RecommendRequest request) {
        var questions = questionRepository.findTop20BySubjectIdAndDifficultyContainingIgnoreCase(
                request.subjectId(), request.difficulty() == null ? "" : request.difficulty());
        if (!request.variant()) {
            return ApiResponse.ok(questions);
        }
        return ApiResponse.ok(questions.stream()
                .map(question -> questionGenerationPort.generateVariant(question, request.requirement()))
                .toList());
    }
}

