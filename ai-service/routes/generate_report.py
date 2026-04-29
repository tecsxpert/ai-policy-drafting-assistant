from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
import json

generate_report_bp = Blueprint("generate_report", __name__)


@generate_report_bp.route("/generate-report", methods=["POST"])
def generate_report():
    data = request.get_json()

    #  Input validation
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
        # Strict prompt (forces JSON)
        prompt = f"""
You are a senior AI governance and policy expert.

Task:
Generate a structured AI policy report.

STRICT RULES:
- Return ONLY valid JSON (no explanation, no extra text)
- Output must be a JSON object with EXACT keys:
  title, executive_summary, overview, top_items, recommendations
- top_items must be an array of 3–5 key points
- recommendations must be an array of 3–5 actionable suggestions
- Maintain formal and professional tone

Example Output:
{{
  "title": "AI Governance Policy for Healthcare Systems",
  "executive_summary": "This report outlines...",
  "overview": "The system operates in...",
  "top_items": ["Item 1", "Item 2", "Item 3"],
  "recommendations": ["Rec 1", "Rec 2", "Rec 3"]
}}

Input:
{user_input}
"""

        #  Call Groq
        ai_response = call_groq(prompt)

        #  Parse JSON
        try:
            report = json.loads(ai_response)
        except json.JSONDecodeError:
            return jsonify({
                "status": "error",
                "message": "Invalid JSON returned from AI",
                "raw_output": ai_response
            }), 500

        #  Validate structure
        required_keys = {
            "title",
            "executive_summary",
            "overview",
            "top_items",
            "recommendations"
        }

        if not isinstance(report, dict) or not required_keys.issubset(report.keys()):
            return jsonify({
                "status": "error",
                "message": "Missing required fields in report",
                "raw_output": ai_response
            }), 500

        #  Validate arrays
        if not isinstance(report["top_items"], list) or not isinstance(report["recommendations"], list):
            return jsonify({
                "status": "error",
                "message": "top_items and recommendations must be arrays"
            }), 500

        # Optional stricter validation
        if not (3 <= len(report["top_items"]) <= 5):
            return jsonify({
                "status": "error",
                "message": "top_items must have 3–5 items"
            }), 500

        if not (3 <= len(report["recommendations"]) <= 5):
            return jsonify({
                "status": "error",
                "message": "recommendations must have 3–5 items"
            }), 500

        # Final response
        return jsonify({
            "status": "success",
            "data": report
        }), 200

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500