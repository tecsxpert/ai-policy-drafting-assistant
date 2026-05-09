from flask import Flask
from flask_cors import CORS
from dotenv import load_dotenv
import os

from services.rag_pipeline import ingest_documents, collection

# Load environment variables
load_dotenv()


def create_app():
    app = Flask(__name__)
    CORS(app)

    # ---- Root Route (Fixes 404 on http://127.0.0.1:5000/) ----
    @app.route("/")
    def home():
        return {"message": "AI Service Running"}

    # ---- Health Check ----
    @app.route("/health")
    def health():
        return {"status": "ok"}

    # ---- Initialize RAG ----
    # Prevent double execution in Flask debug mode
    should_init = (not app.debug) or (os.environ.get("WERKZEUG_RUN_MAIN") == "true")

    if should_init:
        with app.app_context():
            try:
                existing_docs = collection.count()

                if existing_docs == 0:
                    print("No documents found. Ingesting into ChromaDB...")
                    ingest_documents()
                else:
                    print(f"ChromaDB already has {existing_docs} documents.")

            except Exception as e:
                print("Error checking ChromaDB:", e)

                try:
                    ingest_documents()
                except Exception as ie:
                    print("Failed to ingest documents:", ie)

    # ---- Import Blueprints ----
    from routes.describe import describe_bp
    from routes.recommend import recommend_bp
    from routes.categorise import categorise_bp
    from routes.generate_report import generate_report_bp
    from routes.analyse_document import analyse_document_bp

    # ---- Register Blueprints ----
    app.register_blueprint(describe_bp, url_prefix="/api")
    app.register_blueprint(recommend_bp, url_prefix="/api")
    app.register_blueprint(categorise_bp, url_prefix="/api")
    app.register_blueprint(generate_report_bp, url_prefix="/api")
    app.register_blueprint(analyse_document_bp, url_prefix="/api")

    return app


# ---- Entry Point ----
if __name__ == "__main__":
    app = create_app()
    app.run(host="0.0.0.0", port=5000, debug=True)