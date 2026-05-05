"""
Flask AI Service Entry Point — Tool-30 AI Policy Drafting Assistant
Includes: Rate limiting, security headers, input sanitisation, CORS
"""

from flask import Flask, jsonify
from flask_cors import CORS
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_talisman import Talisman
from dotenv import load_dotenv
import os
import logging

# Load environment variables
load_dotenv()

# Configure logging — never log PII
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(name)s: %(message)s'
)
logger = logging.getLogger(__name__)


def create_app(testing=False):
    app = Flask(__name__)
    CORS(app)

    app.config["TESTING"] = testing

    # ─── Rate Limiting (AI Developer 3) ───────────────────────────────
    # 30 req/min default, per IP address
    limiter = Limiter(
        get_remote_address,
        app=app,
        default_limits=["30 per minute"],
        storage_uri="memory://",
        strategy="fixed-window"
    )

    # ─── Security Headers via flask-talisman (AI Developer 3) ─────────
    Talisman(app,
        force_https=False,                    # False for local dev
        strict_transport_security=False,      # False for local dev
        content_security_policy=False,        # Disabled — frontend handles CSP
        frame_options='DENY',                 # Prevents clickjacking
        x_content_type_options=True,          # Prevents MIME sniffing
        referrer_policy='strict-origin-when-cross-origin',
        session_cookie_secure=False           # False for local dev (no HTTPS)
    )

    # ─── Custom error handler for rate limit exceeded ─────────────────
    @app.errorhandler(429)
    def rate_limit_exceeded(e):
        return jsonify({
            "error": "Rate limit exceeded",
            "message": "Too many requests. Please try again later.",
            "retry_after": e.description
        }), 429

    # ─── ChromaDB document ingestion (AI Developer 1) ─────────────────
    if not testing:
        with app.app_context():
            try:
                from services.rag_pipeline import ingest_documents, collection
                existing_docs = collection.count()

                if existing_docs == 0:
                    logger.info("No documents found. Ingesting into ChromaDB...")
                    ingest_documents()
                else:
                    logger.info(
                        f"ChromaDB already has {existing_docs} documents. "
                        f"Skipping ingestion."
                    )
            except Exception as e:
                logger.error(f"Error checking ChromaDB: {e}")
                try:
                    from services.rag_pipeline import ingest_documents
                    ingest_documents()
                except Exception as e2:
                    logger.error(f"Ingestion also failed: {e2}")

    # ─── Import and register all route blueprints ─────────────────────
    from routes.describe import describe_bp
    from routes.recommend import recommend_bp
    from routes.categorise import categorise_bp
    from routes.generate_report import generate_report_bp
    from routes.analyse_document import analyse_document_bp
    from routes.batch_process import batch_process_bp

    app.register_blueprint(describe_bp, url_prefix="/api")
    app.register_blueprint(recommend_bp, url_prefix="/api")
    app.register_blueprint(categorise_bp, url_prefix="/api")
    app.register_blueprint(generate_report_bp, url_prefix="/api")
    app.register_blueprint(analyse_document_bp, url_prefix="/api")
    app.register_blueprint(batch_process_bp, url_prefix="/api")

    # ─── Apply stricter rate limit to /generate-report (AI Developer 3)
    limiter.limit("10 per minute")(generate_report_bp)

    # ─── Health check endpoint ────────────────────────────────────────
    @app.route("/health", methods=["GET"])
    def health():
        return jsonify({"status": "ok"}), 200

    return app


# ─── Entry point ──────────────────────────────────────────────────────
if __name__ == "__main__":
    app = create_app()
    app.run(host="0.0.0.0", port=5000, debug=True)
