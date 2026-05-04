from flask import Blueprint, request, jsonify
from flask import Response, stream_with_context 
from services.groq_client import call_groq
import json
import time

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
    

#  DAY 8 NEW FEATURE: SSE STREAMING ENDPOINT ADDED


@generate_report_bp.route("/generate-report-stream", methods=["POST"])
def generate_report_stream():

    data = request.get_json()

    if not data or "input" not in data:
        return jsonify({"error": "input is required"}), 400

    user_input = data["input"].strip()

    if not user_input:
        return jsonify({"error": "input cannot be empty"}), 400

    try:
        prompt = f"""
You are a senior AI governance expert.

Generate a clear AI policy explanation in simple sentences.
Do NOT return JSON. Return plain text only.

Input:
{user_input}
"""

        ai_response = call_groq(prompt)

        if not ai_response:
            return jsonify({"error": "AI response failed"}), 500

        #  STREAM GENERATOR FUNCTION
        def generate():
            words = ai_response.split()

            for word in words:
                yield f"data: {word}\n\n"
                time.sleep(0.1)  # simulate streaming effect

            yield "data: [DONE]\n\n"

        return Response(
            stream_with_context(generate()),
            mimetype="text/event-stream"
        )

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500    