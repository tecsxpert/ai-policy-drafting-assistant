from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
import json

categorise_bp = Blueprint("categorise", __name__)

@categorise_bp.route("/categorise", methods=["POST"])
def categorise():
    data = request.json
    text = data.get("text", "")

    if not text:
        return jsonify({"error": "Text is required"}), 400

    prompt = f"""
    You are an AI system that classifies policy-related text.

    Categories:
    - Compliance
    - Security
    - HR
    - Finance
    - Operations

    Text:
    {text}

    Return ONLY JSON:
    {{
      "category": "",
      "confidence": 0.0,
      "reasoning": ""
    }}
    """

    response = call_groq(prompt)

    try:
        parsed = json.loads(response)

        return jsonify({
            "category": parsed.get("category"),
            "confidence": parsed.get("confidence"),
            "reasoning": parsed.get("reasoning"),
            "meta": {
                "cached": False
            }
        })

    except Exception:
        return jsonify({
            "message": "Parsing failed",
            "raw": response,
            "is_fallback": True
        })