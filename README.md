# AI Policy Drafting Assistant

---

##  Overview

AI-powered backend system that generates **structured AI policy documents, reports, recommendations, and analysis** using:

-  Groq LLM
-  RAG (ChromaDB)
-  Flask APIs
-  Async Spring Boot integration
-  Structured JSON outputs
-  Batch + Streaming AI processing

---

### Tech Stack

| Layer | Technology |
|------ |------------|
| Backend | Flask |
| AI Model | Groq LLM |
| Vector DB | ChromaDB |
| Embeddings | SentenceTransformers |
| Testing | Pytest |
| Integration | Spring Boot |

---

##  Prerequisites 

Before running the project, ensure you have:

- Python 3.11+
- pip (Python package manager)
- Virtual environment tool (`venv`)
- Git installed
- Groq API key (for LLM access)

---

##  Setup Steps

### 1️ Clone the Repository
```bash
git clone <repo-url>
cd ai-service
```

### 2 Create Virtual Environment
```bash
python -m venv venv
venv\Scripts\activate
```

### 3 Install Dependencies
```bash
pip install -r requirements.txt
```

### 4 Setup Environment Variables
Create a `.env` file in root directory

```bash
GROQ_API_KEY=your_api_key_here
```
---  

## Run Instructions

###  Start Server
```bash
python app.py
```
server runs at - http://localhost:5000

### Run Tests
```bash
pytest -v
```

---

## Endpoint API Reference

1. Describe Policy
POST `/api/describe`

2. Recommend API
POST `/api/recommend`

3. Analyse Document
POST `/api/analyse-document`

4. Generate Report
POST `/api/generate-report`

5. Batch Process 
POST `/api/batch-process`

6. Streaming Report (SSE)
POST `/api/generate-report-stream`

7. Health Check
GET `/health`
