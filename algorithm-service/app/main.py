import base64
import json
import os
from pathlib import Path
from typing import Any

from fastapi import FastAPI
from llama_index.core import Document, Settings, StorageContext, VectorStoreIndex, load_index_from_storage
from llama_index.core.embeddings import MockEmbedding
from llama_index.core.llms import MockLLM
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.llms.openai import OpenAI
from pydantic import BaseModel, Field


DATA_DIR = Path(os.getenv("PANDA_ALGORITHM_DATA_DIR", ".panda_algorithm_data"))
INDEX_ROOT = DATA_DIR / "indexes"
UPLOAD_ROOT = DATA_DIR / "uploads"

app = FastAPI(title="PandaAssistant LlamaIndex Service")


class FileRequest(BaseModel):
    fileName: str
    contentBase64: str


class RecognizedQuestion(BaseModel):
    stem: str
    answer: str
    layoutJson: str = "{}"


class RecognizedQuestionsResponse(BaseModel):
    questions: list[RecognizedQuestion]


class StudentAnswer(BaseModel):
    questionNo: str
    answerText: str
    layoutJson: str = "{}"


class StudentAnswersResponse(BaseModel):
    answers: list[StudentAnswer]


class TagRequest(BaseModel):
    stem: str
    answer: str


class QuestionTags(BaseModel):
    subject: str
    knowledgePoints: list[str]
    difficulty: str = Field(pattern="^(EASY|MEDIUM|HARD)$")


class VariantRequest(BaseModel):
    stem: str
    answer: str
    requirement: str | None = None


class QuestionVariant(BaseModel):
    stem: str
    answer: str
    explanation: str


class MaterialIngestRequest(FileRequest):
    subjectId: int


class RetrieveRequest(BaseModel):
    subjectId: int
    query: str


class EvidenceResponse(BaseModel):
    evidence: list[str]


class GradeRequest(BaseModel):
    subjectId: int
    answers: list[StudentAnswer]


class QuestionScoreDraft(BaseModel):
    questionNo: str
    score: float
    comment: str


class GradingDraft(BaseModel):
    totalScore: float
    questionScores: list[QuestionScoreDraft]
    rubricSummary: str


class StudentScoreSnapshot(BaseModel):
    studentNo: str
    studentName: str
    totalScore: float
    scores: list[QuestionScoreDraft] = []


class ReportRequest(BaseModel):
    scopeType: str
    scopeName: str
    subjectId: int
    scores: list[StudentScoreSnapshot] = []


class ReportResponse(BaseModel):
    reportText: str


@app.on_event("startup")
def configure_llama_index() -> None:
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    if os.getenv("OPENAI_API_KEY"):
        Settings.llm = OpenAI(model=os.getenv("PANDA_LLM_MODEL", "gpt-4o-mini"))
        Settings.embed_model = OpenAIEmbedding(model=os.getenv("PANDA_EMBED_MODEL", "text-embedding-3-small"))
    else:
        Settings.llm = MockLLM(max_tokens=512)
        Settings.embed_model = MockEmbedding(embed_dim=384)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/documents/questions", response_model=RecognizedQuestionsResponse)
def recognize_questions(request: FileRequest) -> RecognizedQuestionsResponse:
    text = extract_text(request)
    prompt = (
        "从下面的题库文本中抽取题目，返回 JSON 数组，每项包含 stem 和 answer。"
        "如果无法可靠抽取，返回一个包含全文摘要题干的数组。\n\n"
        f"{text[:12000]}"
    )
    items = ask_json(prompt, fallback=[{"stem": text[:500] or request.fileName, "answer": "待教师补充"}])
    questions = [
        RecognizedQuestion(stem=str(item.get("stem", "")).strip(), answer=str(item.get("answer", "")).strip())
        for item in ensure_list(items)
        if str(item.get("stem", "")).strip()
    ]
    return RecognizedQuestionsResponse(questions=questions or [RecognizedQuestion(stem=request.fileName, answer="待教师补充")])


@app.post("/documents/student-answers", response_model=StudentAnswersResponse)
def recognize_student_answers(request: FileRequest) -> StudentAnswersResponse:
    text = extract_text(request)
    prompt = (
        "从下面的学生试卷文本中抽取学生答案，返回 JSON 数组，每项包含 questionNo 和 answerText。\n\n"
        f"{text[:12000]}"
    )
    items = ask_json(prompt, fallback=[{"questionNo": "1", "answerText": text[:1000] or request.fileName}])
    answers = [
        StudentAnswer(questionNo=str(item.get("questionNo", "")).strip(), answerText=str(item.get("answerText", "")).strip())
        for item in ensure_list(items)
        if str(item.get("answerText", "")).strip()
    ]
    return StudentAnswersResponse(answers=answers or [StudentAnswer(questionNo="1", answerText=text[:1000])])


@app.post("/questions/tag", response_model=QuestionTags)
def tag_question(request: TagRequest) -> QuestionTags:
    prompt = (
        "请为题目打标签，只返回 JSON：subject 字符串，knowledgePoints 字符串数组，difficulty 为 EASY/MEDIUM/HARD。\n"
        f"题干：{request.stem}\n答案：{request.answer}"
    )
    data = ask_json(prompt, fallback={"subject": "待确认科目", "knowledgePoints": ["待确认知识点"], "difficulty": "MEDIUM"})
    difficulty = str(data.get("difficulty", "MEDIUM")).upper()
    if difficulty not in {"EASY", "MEDIUM", "HARD"}:
        difficulty = "MEDIUM"
    return QuestionTags(
        subject=str(data.get("subject", "待确认科目")),
        knowledgePoints=[str(item) for item in ensure_list(data.get("knowledgePoints"))] or ["待确认知识点"],
        difficulty=difficulty,
    )


@app.post("/questions/variant", response_model=QuestionVariant)
def generate_variant(request: VariantRequest) -> QuestionVariant:
    prompt = (
        "基于原题生成一道变式题，只返回 JSON：stem、answer、explanation。\n"
        f"要求：{request.requirement or '保持知识点一致，数值或情境变化'}\n"
        f"原题：{request.stem}\n原答案：{request.answer}"
    )
    data = ask_json(prompt, fallback={"stem": f"{request.stem}（变式）", "answer": request.answer, "explanation": request.requirement or ""})
    return QuestionVariant(
        stem=str(data.get("stem", f"{request.stem}（变式）")),
        answer=str(data.get("answer", request.answer)),
        explanation=str(data.get("explanation", request.requirement or "")),
    )


@app.post("/materials/ingest")
def ingest_material(request: MaterialIngestRequest) -> dict[str, str]:
    text = extract_text(request)
    subject_dir = upload_subject_dir(request.subjectId)
    file_path = subject_dir / safe_name(request.fileName)
    file_path.write_text(text, encoding="utf-8")
    documents = [Document(text=text, metadata={"file_name": request.fileName, "subject_id": request.subjectId})]
    index = VectorStoreIndex.from_documents(documents)
    persist_dir = index_subject_dir(request.subjectId)
    persist_dir.mkdir(parents=True, exist_ok=True)
    index.storage_context.persist(persist_dir=str(persist_dir))
    return {"status": "ingested"}


@app.post("/materials/retrieve", response_model=EvidenceResponse)
def retrieve_evidence(request: RetrieveRequest) -> EvidenceResponse:
    index = load_subject_index(request.subjectId)
    retriever = index.as_retriever(similarity_top_k=5)
    nodes = retriever.retrieve(request.query)
    return EvidenceResponse(evidence=[node.node.get_content()[:1000] for node in nodes])


@app.post("/grading/grade", response_model=GradingDraft)
def grade(request: GradeRequest) -> GradingDraft:
    evidence = retrieve_evidence(RetrieveRequest(subjectId=request.subjectId, query="评分标准 答案")).evidence
    prompt = (
        "根据参考资料和学生答案评分，只返回 JSON：totalScore 数字，questionScores 数组"
        "（questionNo、score、comment），rubricSummary 字符串。\n"
        f"参考资料：{evidence}\n学生答案：{[answer.model_dump() for answer in request.answers]}"
    )
    data = ask_json(prompt, fallback={"totalScore": 0, "questionScores": [], "rubricSummary": "未找到可用评分标准"})
    return GradingDraft(
        totalScore=float(data.get("totalScore", 0)),
        questionScores=[QuestionScoreDraft(**item) for item in ensure_list(data.get("questionScores")) if isinstance(item, dict)],
        rubricSummary=str(data.get("rubricSummary", "")),
    )


@app.post("/reports/build", response_model=ReportResponse)
def build_report(request: ReportRequest) -> ReportResponse:
    prompt = (
        "生成教师可读的中文学情分析报告，包含整体表现、薄弱知识点、教学建议和后续练习建议。\n"
        f"范围：{request.scopeType} {request.scopeName}\n科目ID：{request.subjectId}\n成绩："
        f"{[score.model_dump() for score in request.scores]}"
    )
    return ReportResponse(reportText=ask_text(prompt))


def extract_text(request: FileRequest) -> str:
    raw = base64.b64decode(request.contentBase64)
    for encoding in ("utf-8", "gb18030"):
        try:
            return raw.decode(encoding).strip()
        except UnicodeDecodeError:
            continue
    return f"{request.fileName}\n（二进制文件已接收，当前服务未配置 OCR/Office/PDF 解析器。）"


def ask_text(prompt: str) -> str:
    response = Settings.llm.complete(prompt)
    return str(response).strip()


def ask_json(prompt: str, fallback: Any) -> Any:
    text = ask_text(prompt)
    start = text.find("{")
    array_start = text.find("[")
    if array_start != -1 and (start == -1 or array_start < start):
        start = array_start
    if start == -1:
        return fallback
    end = max(text.rfind("}"), text.rfind("]"))
    if end <= start:
        return fallback
    try:
        return json.loads(text[start : end + 1])
    except json.JSONDecodeError:
        return fallback


def ensure_list(value: Any) -> list[Any]:
    if value is None:
        return []
    return value if isinstance(value, list) else [value]


def safe_name(file_name: str) -> str:
    return Path(file_name).name or "material.txt"


def upload_subject_dir(subject_id: int) -> Path:
    path = UPLOAD_ROOT / str(subject_id)
    path.mkdir(parents=True, exist_ok=True)
    return path


def index_subject_dir(subject_id: int) -> Path:
    return INDEX_ROOT / str(subject_id)


def load_subject_index(subject_id: int) -> VectorStoreIndex:
    persist_dir = index_subject_dir(subject_id)
    if not persist_dir.exists():
        return VectorStoreIndex.from_documents([Document(text="当前科目还没有入库资料。")])
    storage_context = StorageContext.from_defaults(persist_dir=str(persist_dir))
    return load_index_from_storage(storage_context)
