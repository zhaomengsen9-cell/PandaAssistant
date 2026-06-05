package com.pandaassistant.subject;

import jakarta.validation.constraints.NotBlank;

public class SubjectDtos {
    public record CreateSubjectRequest(@NotBlank String name, String description) {
    }
}

