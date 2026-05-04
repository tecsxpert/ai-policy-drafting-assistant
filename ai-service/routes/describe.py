from flask import Blueprint, request, jsonify
from datetime import datetime
from prompts.describe_prompt import build_describe_prompt
from services.groq_client import call_groq

describe_bp = Blueprint("describe", __name__)


@describe_bp.route("/describe", methods=["POST"])
def describe():
    data = request.get_json()

    if not data or not isinstance(data.get("input"), str) or not data["input"].strip():
        return jsonify({"error": "Invalid or empty input"}), 400

    user_input = data["input"].strip()

    try:
        prompt = build_describe_prompt(user_input)

        ai_response = call_groq(prompt)

        return jsonify({
            "status": "success",
            "data": {
                "input": user_input,
                "output": ai_response,
                "generated_at": datetime.utcnow().isoformat()
            }
        }), 200

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500