package com.pandaassistant.grading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaassistant.algorithm.DocumentUnderstandingPort;
import com.pandaassistant.algorithm.PaperGradingPort;
import com.pandaassistant.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grading")
public class GradingController {
    private final DocumentUnderstandingPort documentUnderstandingPort;
    private final PaperGradingPort paperGradingPort;
    private final GradingResultRepository gradingResultRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/papers")
    public ApiResponse<GradingResult> gradePaper(@RequestParam Long subjectId, @RequestPart MultipartFile file) throws IOException {
        var answers = documentUnderstandingPort.recognizeStudentAnswers(file.getOriginalFilename(), file.getBytes());
        var draft = paperGradingPort.grade(subjectId, answers);
        var result = new GradingResult();
        result.setSubjectId(subjectId);
        result.setFileName(file.getOriginalFilename());
        result.setTotalScore(draft.totalScore());
        result.setResultJson(objectMapper.writeValueAsString(draft));
        return ApiResponse.ok(gradingResultRepository.save(result));
    }

    @PutMapping("/results/{id}")
    public ApiResponse<GradingResult> updateEditedResult(@PathVariable Long id, @RequestBody String editedJson) throws JsonProcessingException {
        objectMapper.readTree(editedJson);
        var result = gradingResultRepository.findById(id).orElseThrow();
        result.setTeacherEditedJson(editedJson);
        return ApiResponse.ok(gradingResultRepository.save(result));
    }
}

