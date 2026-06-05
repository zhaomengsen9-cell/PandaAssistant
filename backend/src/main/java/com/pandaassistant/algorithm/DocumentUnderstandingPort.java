package com.pandaassistant.algorithm;

import java.util.List;

public interface DocumentUnderstandingPort {
    List<RecognizedQuestion> recognizeQuestions(String fileName, byte[] content);

    List<StudentAnswer> recognizeStudentAnswers(String fileName, byte[] content);
}

