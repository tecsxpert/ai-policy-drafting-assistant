from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
import json

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
    # Get request body
    data = request.get_json() or {}

    # Support both old and new request keys
    user_input = data.get("input") or data.get("text")

    # Validate input
    if not user_input or not isinstance(user_input, str):
        return jsonify({
            "status": "error",
            "message": "Invalid input"
        }), 400

    # AI Prompt
    prompt = f"""
Classify the following input into exactly one of these categories:
{', '.join(CATEGORIES)}

Return ONLY valid JSON in this exact format:
{{
    "category": "...",
    "confidence": 0.0,
    "reasoning": "..."
}}

Input:
{user_input}
"""

    try:
        print("Sending prompt to Groq...")

        # Call AI
        response = call_groq(prompt)

        print("Groq Raw Response:", response)

        # Convert AI string response into proper JSON
        try:
            parsed_response = json.loads(response)

        except json.JSONDecodeError:
            # Fallback if AI returns invalid JSON
            parsed_response = {
                "category": "Unknown",
                "confidence": 0.0,
                "reasoning": response
            }

        # Success response
        return jsonify({
            "status": "success",
            "result": parsed_response
        })

    except Exception as e:
        print("Categorise Route Error:", str(e))

        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500