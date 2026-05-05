from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.middleware import sanitise_request

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
@sanitise_request
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
