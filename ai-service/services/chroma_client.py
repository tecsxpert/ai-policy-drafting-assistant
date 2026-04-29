import chromadb
from sentence_transformers import SentenceTransformer

# Load embedding model
model = SentenceTransformer("all-MiniLM-L6-v2")

# Create Chroma client (persistent)
client = chromadb.Client()

# Create or get collection
collection = client.get_or_create_collection(name="policy_docs")


def add_document(doc_id, text):
    embedding = model.encode(text).tolist()

    collection.add(
        ids=[doc_id],
        embeddings=[embedding],
        documents=[text]
    )


def query_document(query_text):
    query_embedding = model.encode(query_text).tolist()

    results = collection.query(
        query_embeddings=[query_embedding],
        n_results=3
    )

    return results