package com.pandaassistant.algorithm;

import java.util.List;

public interface VectorKnowledgeBasePort {
    void ingestMaterial(Long subjectId, String fileName, byte[] content);

    List<String> retrieveEvidence(Long subjectId, String query);
}

