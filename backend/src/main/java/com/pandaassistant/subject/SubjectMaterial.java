package com.pandaassistant.subject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "subject_materials")
public class SubjectMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subjectId;
    private String fileName;
    private String materialType;
    private String ingestStatus;
    private Instant createdAt = Instant.now();
}

