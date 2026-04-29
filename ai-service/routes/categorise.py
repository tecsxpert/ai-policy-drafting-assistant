from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
 day2-day4
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


categorise_bp = Blueprint("categorise", __name__)

CATEGORIES = [
    "Data Privacy",
    "AI Governance",
    "Security",
    "Ethics",
    "Compliance",
    "Risk Management"
]


@categorise_bp.route("/categorise", methods=["POST"])
def categorise():
    data = request.get_json()

    if not data or not isinstance(data.get("input"), str):
        return jsonify({"error": "Invalid input"}), 400

    user_input = data["input"]

    prompt = f"""
Classify the following input into one of these categories:
{", ".join(CATEGORIES)}

Return JSON:
{{
    "category": "...",
    "confidence": 0.0,
    "reasoning": "..."
}}

Input:
{user_input}
"""

    response = call_groq(prompt)

    return jsonify({"result": response})
 main
