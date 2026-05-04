from flask import Flask
 features
from routes.categorise import categorise_bp

app = Flask(_name_)

# Register the route
app.register_blueprint(categorise_bp)

@app.route("/")
def home():
    return {"message": "AI Service Running"}

if _name_ == "_main_":
    app.run(debug=True)

from flask_cors import CORS
from dotenv import load_dotenv
from services.rag_pipeline import ingest_documents, collection
import os
from routes.generate_report import generate_report_bp
from routes.analyse_document import analyse_document_bp

# Load environment variables
load_dotenv()


def create_app():
    app = Flask(__name__)
    CORS(app)

    with app.app_context():
        try:
            existing_docs = collection.count()

            if existing_docs == 0:
                print("No documents found. Ingesting into ChromaDB...")
                ingest_documents()
            else:
                print(f" ChromaDB already has {existing_docs} documents. Skipping ingestion.")

        except Exception as e:
            print(f" Error checking ChromaDB: {e}")
            print(" Attempting ingestion...")
            ingest_documents()

    #  Import all blueprints inside function (best practice)
    from routes.describe import describe_bp
    from routes.recommend import recommend_bp
    from routes.categorise import categorise_bp

    # Register all routes
    app.register_blueprint(describe_bp, url_prefix="/api")
    app.register_blueprint(recommend_bp, url_prefix="/api")
    app.register_blueprint(categorise_bp, url_prefix="/api")
    app.register_blueprint(generate_report_bp, url_prefix="/api")
    app.register_blueprint(analyse_document_bp, url_prefix="/api")

    # Health check
    @app.route("/health", methods=["GET"])
    def health():
        return {"status": "ok"}, 200

    return app


# Entry point
if __name__ == "__main__":
    app = create_app()
    app.run(host="0.0.0.0", port=5000, debug=True)
 main
