from flask import Blueprint, request, jsonify
 features
from services.groq_client import GroqClient

categorise_bp = Blueprint("categorise", _name_)

groq = GroqClient()

@categorise_bp.route("/categorise", methods=["POST"])
def categorise():
    data = request.json
    text = data.get("text")

    prompt = f"""
Categorise this text into:
HR, IT, Finance, Legal, Security

Return JSON:
{{
category:"",
confidence:0.0,
reasoning:""
}}

Text: {text}
"""

    result = groq.generate(prompt)

    return jsonify({"result": result})

from services.groq_client import call_groq

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
