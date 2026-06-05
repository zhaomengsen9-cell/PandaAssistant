package com.pandaassistant.subject;

import com.pandaassistant.algorithm.VectorKnowledgeBasePort;
import com.pandaassistant.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subjects")
public class SubjectController {
    private final SubjectRepository subjectRepository;
    private final SubjectMaterialRepository materialRepository;
    private final VectorKnowledgeBasePort vectorKnowledgeBasePort;

    @GetMapping
    public ApiResponse<List<Subject>> listSubjects() {
        return ApiResponse.ok(subjectRepository.findAll());
    }

    @PostMapping
    public ApiResponse<Subject> createSubject(@Valid @RequestBody SubjectDtos.CreateSubjectRequest request) {
        var subject = new Subject();
        subject.setName(request.name());
        subject.setDescription(request.description());
        return ApiResponse.ok(subjectRepository.save(subject));
    }

    @PostMapping("/{subjectId}/materials")
    public ApiResponse<SubjectMaterial> uploadMaterial(@PathVariable Long subjectId,
                                                       @RequestParam(defaultValue = "REFERENCE") String materialType,
                                                       @RequestPart MultipartFile file) throws IOException {
        vectorKnowledgeBasePort.ingestMaterial(subjectId, file.getOriginalFilename(), file.getBytes());
        var material = new SubjectMaterial();
        material.setSubjectId(subjectId);
        material.setFileName(file.getOriginalFilename());
        material.setMaterialType(materialType);
        material.setIngestStatus("INGESTED");
        return ApiResponse.ok(materialRepository.save(material));
    }

    @GetMapping("/{subjectId}/materials")
    public ApiResponse<List<SubjectMaterial>> listMaterials(@PathVariable Long subjectId) {
        return ApiResponse.ok(materialRepository.findBySubjectId(subjectId));
    }
}

