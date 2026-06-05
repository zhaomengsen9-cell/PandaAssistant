# PandaAssistant

PandaAssistant is an education assistant platform with two first-class modules:

- Question authoring: question-bank import, OCR/layout review, LLM tagging, and question recommendation.
- Grading analytics: subject RAG knowledge bases, paper grading, editable grading results, and learning reports.

## Tech Choice

The frontend uses **React + Vite + TypeScript**.

Reason: this project is closer to a data-heavy admin console than a content site. React has a mature ecosystem for upload flows, editable tables, stateful review screens, and later integration with charting or document annotation tools. TypeScript also helps keep the many DTOs shared with the Spring Boot API predictable.

The backend uses **Spring Boot 3** with Spring Web, Spring Data JPA, validation, Flyway, and PostgreSQL-ready configuration. Algorithms are intentionally represented as interfaces and stub implementations so OCR/layout/RAG/LLM services can be plugged in later.

## Project Layout

```text
PandaAssistant/
  backend/      Spring Boot API
  frontend/     React admin workspace
  docs/         architecture notes
```

## Run Frontend

```bash
cd PandaAssistant/frontend
npm install
npm run dev
```

## Run Backend

Install JDK 17+ and Maven inside your project conda environment first. Do not install them in `base` if you are working in `zms_py3.12`.

If your prompt already shows `(zms_py3.12)`, install directly:

```bash
conda install -c conda-forge openjdk=17 maven
java -version
mvn -version
```

If a new shell cannot run `conda activate`, initialize conda for the current terminal session first:

```bash
source /opt/miniconda3/etc/profile.d/conda.sh
conda activate zms_py3.12
```

You can also avoid shell activation entirely:

```bash
conda run -n zms_py3.12 mvn -version
conda run -n zms_py3.12 mvn spring-boot:run
```

Then start the backend:

```bash
cd PandaAssistant/backend
mvn spring-boot:run
```

If port `8080` is already in use, start on another port:

```bash
SERVER_PORT=8081 mvn spring-boot:run
```

By default the backend uses H2 in-memory storage for local development. Set PostgreSQL environment variables when needed:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/panda_assistant
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

## Algorithm Extension Points

Backend interfaces are in `backend/src/main/java/com/pandaassistant/algorithm`:

- `DocumentUnderstandingPort`: layout and OCR for Word/PDF/paper images.
- `QuestionTaggingPort`: LLM tagging for subject, knowledge points, and difficulty.
- `QuestionGenerationPort`: variant question generation.
- `VectorKnowledgeBasePort`: subject vector database/RAG ingestion and retrieval.
- `PaperGradingPort`: answer extraction and grading.
- `LearningReportPort`: fixed-format learning analysis reports.
