package com.pandaassistant.algorithm;

import com.pandaassistant.question.Question;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.algorithm", name = "mode", havingValue = "llamaindex")
@EnableConfigurationProperties(LlamaIndexAlgorithmProperties.class)
public class LlamaIndexAlgorithmAdapter implements DocumentUnderstandingPort, QuestionTaggingPort,
        QuestionGenerationPort, VectorKnowledgeBasePort, PaperGradingPort, LearningReportPort {
    private final RestClient restClient;

    public LlamaIndexAlgorithmAdapter(LlamaIndexAlgorithmProperties properties, RestClient.Builder builder) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.timeout());
        requestFactory.setReadTimeout(properties.timeout());
        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setAccept(List.of(MediaType.APPLICATION_JSON));
                    return execution.execute(request, body);
                })
                .build();
    }

    @Override
    public List<RecognizedQuestion> recognizeQuestions(String fileName, byte[] content) {
        var response = post("/documents/questions", new FileRequest(fileName, encode(content)), RecognizedQuestionsResponse.class);
        return response == null ? List.of() : response.questions();
    }

    @Override
    public List<StudentAnswer> recognizeStudentAnswers(String fileName, byte[] content) {
        var response = post("/documents/student-answers", new FileRequest(fileName, encode(content)), StudentAnswersResponse.class);
        return response == null ? List.of() : response.answers();
    }

    @Override
    public QuestionTags tag(String stem, String answer) {
        return post("/questions/tag", new TagRequest(stem, answer), QuestionTags.class);
    }

    @Override
    public QuestionVariant generateVariant(Question sourceQuestion, String requirement) {
        return post("/questions/variant",
                new VariantRequest(sourceQuestion.getStem(), sourceQuestion.getAnswer(), requirement),
                QuestionVariant.class);
    }

    @Override
    public void ingestMaterial(Long subjectId, String fileName, byte[] content) {
        post("/materials/ingest", new MaterialIngestRequest(subjectId, fileName, encode(content)), Void.class);
    }

    @Override
    public List<String> retrieveEvidence(Long subjectId, String query) {
        var response = post("/materials/retrieve", new RetrieveRequest(subjectId, query), EvidenceResponse.class);
        return response == null ? List.of() : response.evidence();
    }

    @Override
    public GradingDraft grade(Long subjectId, List<StudentAnswer> answers) {
        var draft = post("/grading/grade", new GradeRequest(subjectId, answers), GradingDraft.class);
        return draft == null ? new GradingDraft(BigDecimal.ZERO, List.of(), "") : draft;
    }

    @Override
    public String buildReport(String scopeType, String scopeName, Long subjectId, List<StudentScoreSnapshot> scores) {
        var response = post("/reports/build", new ReportRequest(scopeType, scopeName, subjectId, scores), ReportResponse.class);
        return response == null ? "" : response.reportText();
    }

    private <T> T post(String path, Object request, Class<T> responseType) {
        return restClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(responseType);
    }

    private String encode(byte[] content) {
        return Base64.getEncoder().encodeToString(content);
    }

    private record FileRequest(String fileName, String contentBase64) {
    }

    private record RecognizedQuestionsResponse(List<RecognizedQuestion> questions) {
    }

    private record StudentAnswersResponse(List<StudentAnswer> answers) {
    }

    private record TagRequest(String stem, String answer) {
    }

    private record VariantRequest(String stem, String answer, String requirement) {
    }

    private record MaterialIngestRequest(Long subjectId, String fileName, String contentBase64) {
    }

    private record RetrieveRequest(Long subjectId, String query) {
    }

    private record EvidenceResponse(List<String> evidence) {
    }

    private record GradeRequest(Long subjectId, List<StudentAnswer> answers) {
    }

    private record ReportRequest(String scopeType, String scopeName, Long subjectId, List<StudentScoreSnapshot> scores) {
    }

    private record ReportResponse(String reportText) {
    }
}
