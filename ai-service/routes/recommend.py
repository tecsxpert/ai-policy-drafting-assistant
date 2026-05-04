from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
import json

recommend_bp = Blueprint("recommend", __name__)


@recommend_bp.route("/recommend", methods=["POST"])
def recommend():
    data = request.get_json()

    #  Input Validation
    if not data:
        return jsonify({"error": "Request body is required"}), 400

    if "input" not in data:
        return jsonify({"error": "'input' field is required"}), 400

    if not isinstance(data["input"], str):
        return jsonify({"error": "Input must be a string"}), 400

    user_input = data["input"].strip()

    if not user_input:
        return jsonify({"error": "Input cannot be empty"}), 400

    try:
        # Strict Prompt
        prompt = f"""
You are an AI governance and policy expert.

Task:
Based on the input, generate EXACTLY 3 actionable recommendations.

STRICT RULES:
- Return ONLY valid JSON (no explanation, no extra text)
- Output must be a JSON array
- Each object must contain:
  - action_type (short category)
  - description (clear actionable step)
  - priority (HIGH, MEDIUM, or LOW)

Example Output:
[
  {{
    "action_type": "Data Governance",
    "description": "Implement role-based access control for sensitive datasets",
    "priority": "HIGH"
  }}
]

Input:
{user_input}
"""

        # Call Groq
        ai_response = call_groq(prompt)

        #  Parse JSON
        try:
            recommendations = json.loads(ai_response)
        except json.JSONDecodeError:
            return jsonify({
                "status": "error",
                "message": "Invalid JSON returned from AI",
                "raw_output": ai_response
            }), 500

        # Validate list size
        if not isinstance(recommendations, list) or len(recommendations) != 3:
            return jsonify({
                "status": "error",
                "message": "AI did not return exactly 3 recommendations",
                "raw_output": ai_response
            }), 500

        #  Validate structure + priority
        VALID_PRIORITIES = {"HIGH", "MEDIUM", "LOW"}

        if any(
            not isinstance(item, dict) or
            not all(k in item for k in ["action_type", "description", "priority"]) or
            item["priority"] not in VALID_PRIORITIES
            for item in recommendations
        ):
            return jsonify({
                "status": "error",
                "message": "Invalid recommendation structure or priority values",
                "raw_output": ai_response
            }), 500

        # Final Response
        return jsonify({
            "status": "success",
            "data": {
                "recommendations": recommendations
            }
        }), 200

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500