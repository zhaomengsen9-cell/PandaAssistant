# Architecture Notes

## Core Flow

1. Teachers upload Word/PDF question-bank files.
2. Backend stores an import task and calls `DocumentUnderstandingPort`.
3. Recognized questions are returned as editable drafts.
4. Edited questions are saved and tagged through `QuestionTaggingPort`.
5. Recommendation queries can request original questions or generated variants.
6. Subject materials are ingested through `VectorKnowledgeBasePort`.
7. Student papers are uploaded, OCR/layout is performed, and `PaperGradingPort` produces editable grading results.
8. Class or grade reports are produced by `LearningReportPort`.

## Database Scope

The initial schema includes:

- `subjects`
- `knowledge_points`
- `questions`
- `question_import_tasks`
- `subject_materials`
- `exam_papers`
- `grading_results`
- `student_exam_scores`
- `student_question_scores`
- `learning_reports`

Algorithm outputs are stored as JSON text for now, which keeps the data model flexible while OCR/RAG/LLM contracts are still evolving.

