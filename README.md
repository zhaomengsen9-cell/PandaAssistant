# PandaAssistant

PandaAssistant 是一个面向教师的智能教学辅助平台，当前已完成前后端基础框架搭建，并为后续 OCR、版面分析、RAG、阅卷和大模型分析预留接口。

## 功能模块

- 出题系统：支持题库 Word/PDF/PPT 上传、layout 与 OCR 识别、识别后编辑、题干和答案编辑、大模型自动打标签、题目推荐、原题/变式题选择。
- 阅卷分析系统：支持按科目建设向量数据库、上传课本/参考资料/试卷答案作为 RAG 基础、上传学生试卷并评阅、评阅结果编辑、班级或年级维度学情分析报告。

## 技术选型

前端选择 **React + Vite + TypeScript**。

原因：PandaAssistant 更像一个数据密集型教师工作台，需要大量上传流程、编辑表单、结果审核、可编辑表格、后续图表和文档批注能力。React 生态在这些后台管理和复杂交互场景里更成熟，TypeScript 也方便前后端 DTO 对齐。

后端选择 **Spring Boot 3**，目前包含 Spring Web、Spring Data JPA、Validation、Flyway，并默认使用 H2 内存数据库进行本地开发。后续可以通过环境变量切换到 PostgreSQL。

算法部分暂不实现具体逻辑，目前以接口和 stub 占位实现的方式保留扩展点，后续可以接入 OCR、版面分析、向量数据库、RAG 和大模型服务。

## 项目结构

```text
PandaAssistant/
  backend/      Spring Boot 后端接口
  frontend/     React 前端工作台
  docs/         架构说明文档
```

## 启动前端
```bash
cd /opt/mengsen/PandaAssistant/frontend
npm install
npm run dev
```

前端默认运行在：

```text
http://localhost:5173
```

## 启动后端

后端需要 JDK 17+ 和 Maven。建议安装在  conda 环境里，不要安装到 base 或系统 apt。

```bash
conda install -c conda-forge openjdk=17 maven
java -version
mvn -version
```

正常启动后端：

```bash
cd /opt/mengsen/PandaAssistant/backend
mvn spring-boot:run
```

后端默认运行在：

```text
http://localhost:8080
```

如果 `8080` 端口已经被占用，可以换端口启动：

```bash
SERVER_PORT=8081 mvn spring-boot:run
```

By default the backend uses H2 in-memory storage for local development. Set PostgreSQL environment variables when needed:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/panda_assistant
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

数据库初始化脚本：

```text
backend/src/main/resources/db/migration/V1__init_schema.sql
```

## 算法接口预留

后端算法接口位于：

```text
backend/src/main/java/com/pandaassistant/algorithm
```

当前预留接口包括：

- `DocumentUnderstandingPort`: layout and OCR for Word/PDF/paper images.
- `QuestionTaggingPort`: LLM tagging for subject, knowledge points, and difficulty.
- `QuestionGenerationPort`: variant question generation.
- `VectorKnowledgeBasePort`: subject vector database/RAG ingestion and retrieval.
- `PaperGradingPort`: answer extraction and grading.
- `LearningReportPort`: fixed-format learning analysis reports.
