import chromadb
from sentence_transformers import SentenceTransformer

#  Initialize ChromaDB (persistent storage)
client = chromadb.PersistentClient(path="./chroma_db")
collection = client.get_or_create_collection(name="ai_policy_docs")

# Global model (lazy-loaded)
model = None  # global

#  Load model only once
def load_model():
    global model
    if model is None:
        print("🔄 Loading embedding model...")
        model = SentenceTransformer("all-MiniLM-L6-v2")
        print("✅ Model loaded")

#  Safe getter 
def get_model():
    global model
    if model is None:
        load_model()
    return model

#  Step 1: Chunking function (500 chars, 50 overlap)
def chunk_text(text, chunk_size=500, overlap=50):
    chunks = []
    start = 0

    while start < len(text):
        end = start + chunk_size
        chunk = text[start:end]
        chunks.append(chunk)
        start += (chunk_size - overlap)

    return chunks


# Load documents from file
def load_documents(file_path="data/docs.txt"):
    with open(file_path, "r", encoding="utf-8") as f:
        return f.read()


# Add documents to ChromaDB
def ingest_documents():
    model = get_model() # ensure model loaded

    text = load_documents()
    chunks = chunk_text(text)

    for i, chunk in enumerate(chunks):
        embedding = model.encode(chunk).tolist()

        collection.add(
            documents=[chunk],
            embeddings=[embedding],
            ids=[f"doc_{i}"]
        )

    print(f" Ingested {len(chunks)} chunks into ChromaDB")


#  Step 4: Query function
def query_documents(query, top_k=3):
    model = get_model()   #  ensure model loaded

    query_embedding = model.encode(query).tolist()

    results = collection.query(
        query_embeddings=[query_embedding],
        n_results=top_k
    )

    return results["documents"][0]