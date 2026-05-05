from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.middleware import sanitise_request
import time
import json

batch_process_bp = Blueprint("batch_process", __name__)


@batch_process_bp.route("/batch-process", methods=["POST"])
@sanitise_request
def batch_process():
    data = request.get_json()

    #  Validation
    if not data or "items" not in data:
        return jsonify({"error": "items field is required"}), 400

    if not isinstance(data["items"], list):
        return jsonify({"error": "items must be a list"}), 400

    items = data["items"]

    if len(items) == 0:
        return jsonify({"error": "items cannot be empty"}), 400

    if len(items) > 20:
        return jsonify({"error": "Maximum 20 items allowed"}), 400

    results = []

    # Process each item
    for item in items:
        if not isinstance(item, str) or not item.strip():
            results.append({
                "input": item,
                "error": "Invalid input"
            })
            continue

        try:
            prompt = f"Summarize: {item}"

            ai_response = call_groq(prompt)

            results.append({
                "input": item,
                "output": ai_response
            })

        except Exception as e:
            results.append({
                "input": item,
                "error": str(e)
            })

        #  100ms delay
        time.sleep(0.1)

    return jsonify({
        "status": "success",
        "results": results
    }), 200