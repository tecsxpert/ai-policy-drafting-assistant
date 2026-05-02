import chromadb

client = chromadb.PersistentClient(path="./chroma_db")

collection = client.get_or_create_collection("policy_docs")

def add_doc(id, text):
    collection.add(
        ids=[id],
        documents=[text]
    )

def search(query):
    result = collection.query(
        query_texts=[query],
        n_results=3
    )
    return result