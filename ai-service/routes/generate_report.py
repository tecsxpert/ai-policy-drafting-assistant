from flask import Blueprint, request, jsonify, Response, stream_with_context
from services.groq_client import call_groq
import json
import time

generate_report_bp = Blueprint("generate_report", __name__)


# --------------------------------------------------
# MAIN REPORT GENERATION ENDPOINT
# URL: POST /api/generate_report
# --------------------------------------------------
@generate_report_bp.route("/generate_report", methods=["POST"])
def generate_report():
    data = request.get_json()

    # ---- Input Validation ----
    if not data:
        return jsonify({
            "status": "error",
            "message": "Request body is required"
        }), 400

    # Support multiple field names
    user_input = (
        data.get("input")
        or data.get("text")
        or data.get("policy_title")
    )

    if not user_input:
        return jsonify({
            "status": "error",
            "message": "'input' field is required"
        }), 400

    if not isinstance(user_input, str):
        return jsonify({
            "status": "error",
            "message": "Input must be a string"
        }), 400

    user_input = user_input.strip()

    if not user_input:
        return jsonify({
            "status": "error",
            "message": "Input cannot be empty"
        }), 400

    try:
        # ---- Strict Prompt ----
        prompt = f"""
You are a senior AI governance and policy expert.

Task:
Generate a structured AI policy report.

STRICT RULES:
- Return ONLY valid JSON
- No markdown
- No explanations outside JSON
- Output must contain EXACT keys:
title, executive_summary, overview, top_items, recommendations

- top_items must contain 3 to 5 items
- recommendations must contain 3 to 5 items
- Maintain professional enterprise tone

Example:
{{
  "title": "AI Governance Policy for Enterprise Systems",
  "executive_summary": "This report outlines...",
  "overview": "The organization requires...",
  "top_items": [
    "Item 1",
    "Item 2",
    "Item 3"
  ],
  "recommendations": [
    "Recommendation 1",
    "Recommendation 2",
    "Recommendation 3"
  ]
}}

Input:
{user_input}
"""

        print("Sending generate_report prompt to Groq...")

        # ---- AI Call ----
        ai_response = call_groq(prompt)

        print("Groq Raw Response:", ai_response)

        # ---- Parse JSON ----
        try:
            report = json.loads(ai_response)

        except json.JSONDecodeError:
            return jsonify({
                "status": "error",
                "message": "Invalid JSON returned from AI",
                "raw_output": ai_response
            }), 500

        # ---- Validate Required Keys ----
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

        # ---- Validate Arrays ----
        if not isinstance(report["top_items"], list):
            return jsonify({
                "status": "error",
                "message": "top_items must be an array"
            }), 500

        if not isinstance(report["recommendations"], list):
            return jsonify({
                "status": "error",
                "message": "recommendations must be an array"
            }), 500

        # ---- Validate Length ----
        if not (3 <= len(report["top_items"]) <= 5):
            return jsonify({
                "status": "error",
                "message": "top_items must have 3 to 5 items"
            }), 500

        if not (3 <= len(report["recommendations"]) <= 5):
            return jsonify({
                "status": "error",
                "message": "recommendations must have 3 to 5 items"
            }), 500

        # ---- Success ----
        return jsonify({
            "status": "success",
            "data": report
        }), 200

    except Exception as e:
        print("Generate Report Error:", str(e))

        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500


# --------------------------------------------------
# STREAMING REPORT ENDPOINT (DAY 8)
# URL: POST /api/generate_report_stream
# --------------------------------------------------
@generate_report_bp.route("/generate_report_stream", methods=["POST"])
def generate_report_stream():

    data = request.get_json()

    if not data:
        return jsonify({
            "status": "error",
            "message": "Request body required"
        }), 400

    user_input = data.get("input") or data.get("text")

    if not user_input:
        return jsonify({
            "status": "error",
            "message": "input is required"
        }), 400

    user_input = user_input.strip()

    if not user_input:
        return jsonify({
            "status": "error",
            "message": "input cannot be empty"
        }), 400

    try:
        prompt = f"""
You are a senior AI governance expert.

Generate a clear AI policy explanation in simple professional language.

Return plain text only.
No JSON.

Input:
{user_input}
"""

        ai_response = call_groq(prompt)

        if not ai_response:
            return jsonify({
                "status": "error",
                "message": "AI response failed"
            }), 500

        # ---- Streaming Generator ----
        def generate():
            words = ai_response.split()

            for word in words:
                yield f"data: {word}\n\n"
                time.sleep(0.1)

            yield "data: [DONE]\n\n"

        return Response(
            stream_with_context(generate()),
            mimetype="text/event-stream"
        )

    except Exception as e:
        print("Streaming Error:", str(e))

        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500