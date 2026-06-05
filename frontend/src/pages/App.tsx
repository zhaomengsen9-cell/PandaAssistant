import { useMemo, useState } from 'react';
import { BookOpen, ClipboardCheck, Database, FileUp, LineChart, Search, Sparkles } from 'lucide-react';

type Tab = 'bank' | 'recommend' | 'subjects' | 'grading' | 'analytics';

const tabs: Array<{ key: Tab; label: string; icon: typeof BookOpen }> = [
  { key: 'bank', label: '题库建设', icon: FileUp },
  { key: 'recommend', label: '题目推荐', icon: Search },
  { key: 'subjects', label: '科目资料库', icon: Database },
  { key: 'grading', label: '试卷评阅', icon: ClipboardCheck },
  { key: 'analytics', label: '学情分析', icon: LineChart }
];

export function App() {
  const [tab, setTab] = useState<Tab>('bank');
  const activeTitle = useMemo(() => tabs.find((item) => item.key === tab)?.label, [tab]);

  return (
    <main className="shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">PA</div>
          <div>
            <strong>PandaAssistant</strong>
            <span>智能教学工作台</span>
          </div>
        </div>

        <nav className="nav">
          {tabs.map((item) => {
            const Icon = item.icon;
            return (
              <button className={tab === item.key ? 'active' : ''} key={item.key} onClick={() => setTab(item.key)}>
                <Icon size={18} />
                {item.label}
              </button>
            );
          })}
        </nav>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div>
            <span className="eyebrow">当前模块</span>
            <h1>{activeTitle}</h1>
          </div>
          <div className="status-pill">
            <Sparkles size={16} />
            算法接口已预留
          </div>
        </header>

        {tab === 'bank' && <QuestionBank />}
        {tab === 'recommend' && <Recommendation />}
        {tab === 'subjects' && <SubjectKnowledge />}
        {tab === 'grading' && <PaperGrading />}
        {tab === 'analytics' && <Analytics />}
      </section>
    </main>
  );
}

function QuestionBank() {
  return (
    <div className="grid two">
      <UploadPanel title="上传题库 Word/PDF" accept=".doc,.docx,.pdf" />
      <section className="panel">
        <div className="panel-head">
          <h2>识别后编辑</h2>
          <span>layout + OCR draft</span>
        </div>
        <label>题干</label>
        <textarea defaultValue="识别出的题干会显示在这里，教师可以修改后入库。" />
        <label>答案</label>
        <textarea defaultValue="识别出的参考答案会显示在这里，也支持编辑。" />
        <div className="form-row">
          <input placeholder="科目：数学" />
          <input placeholder="知识点：函数, 导数" />
          <select defaultValue="MEDIUM">
            <option value="EASY">简单</option>
            <option value="MEDIUM">中等</option>
            <option value="HARD">困难</option>
          </select>
        </div>
        <button className="primary">保存到题库</button>
      </section>
    </div>
  );
}

function Recommendation() {
  return (
    <div className="grid two">
      <section className="panel">
        <div className="panel-head">
          <h2>出题要求</h2>
          <span>匹配原题或生成变式</span>
        </div>
        <input placeholder="科目" />
        <input placeholder="知识点，例如：牛顿第二定律" />
        <select defaultValue="original">
          <option value="original">题库原题</option>
          <option value="variant">变式题</option>
        </select>
        <select defaultValue="MEDIUM">
          <option value="EASY">简单</option>
          <option value="MEDIUM">中等</option>
          <option value="HARD">困难</option>
        </select>
        <button className="primary">生成推荐</button>
      </section>
      <section className="panel result-list">
        <h2>推荐结果</h2>
        {['基础概念题', '综合应用题', '变式训练题'].map((item) => (
          <article className="question-card" key={item}>
            <strong>{item}</strong>
            <p>后端将根据科目、知识点和难度返回候选题，变式题由算法接口补齐。</p>
          </article>
        ))}
      </section>
    </div>
  );
}

function SubjectKnowledge() {
  return (
    <div className="grid two">
      <section className="panel">
        <div className="panel-head">
          <h2>新建科目</h2>
          <span>独立向量库</span>
        </div>
        <input placeholder="科目名称，例如：高中物理" />
        <textarea placeholder="科目说明" />
        <button className="primary">创建科目</button>
      </section>
      <UploadPanel title="上传课本/参考资料/答案" accept=".pdf,.doc,.docx,.txt" />
    </div>
  );
}

function PaperGrading() {
  return (
    <div className="grid two">
      <UploadPanel title="上传学生试卷" accept=".pdf,.jpg,.png,.doc,.docx" />
      <section className="panel">
        <div className="panel-head">
          <h2>评阅结果编辑</h2>
          <span>RAG + rubric draft</span>
        </div>
        <input placeholder="学生学号" />
        <input placeholder="学生姓名" />
        <textarea defaultValue="这里显示自动评阅结果，教师可修订每题得分和评语。" />
        <button className="primary">保存评阅结果</button>
      </section>
    </div>
  );
}

function Analytics() {
  return (
    <div className="grid two">
      <section className="panel">
        <div className="panel-head">
          <h2>成绩数据</h2>
          <span>总分 + 每题得分</span>
        </div>
        <div className="form-row">
          <input placeholder="班级/年级" />
          <input placeholder="考试名称" />
        </div>
        <textarea defaultValue="学号,姓名,总分,第1题,第2题,第3题&#10;2026001,张三,86,10,8,12" />
        <button className="primary">生成报告</button>
      </section>
      <section className="panel report">
        <h2>固定格式报告</h2>
        <p>1. 班级整体表现：等待后端分析生成。</p>
        <p>2. 知识点掌握：按题目知识点聚合得分率。</p>
        <p>3. 教学建议：针对薄弱知识点给出后续教学方向。</p>
      </section>
    </div>
  );
}

function UploadPanel({ title, accept }: { title: string; accept: string }) {
  return (
    <section className="panel upload-panel">
      <div className="panel-head">
        <h2>{title}</h2>
        <span>{accept}</span>
      </div>
      <label className="dropzone">
        <FileUp size={30} />
        <strong>选择文件上传</strong>
        <span>后端会调用预留的 layout/OCR/RAG 接口处理</span>
        <input type="file" accept={accept} />
      </label>
      <button className="primary">开始处理</button>
    </section>
  );
}

