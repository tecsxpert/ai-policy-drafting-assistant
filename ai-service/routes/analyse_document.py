from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
import json

analyse_document_bp = Blueprint("analyse_document", __name__)


@analyse_document_bp.route("/analyse-document", methods=["POST"])
def analyse_document():

    data = request.get_json()

 
    # Input Validation
    if not data:
        return jsonify({"error": "Request body is required"}), 400

    if "text" not in data:
        return jsonify({"error": "'text' field is required"}), 400

    if not isinstance(data["text"], str):
        return jsonify({"error": "text must be a string"}), 400

    text = data["text"].strip()

    if not text:
        return jsonify({"error": "text cannot be empty"}), 400

    try:

      
        # AI Prompt Engineering
        prompt = f"""
You are an AI document analysis expert.

Your task is to analyze the given document and extract:

1. Key insights
2. Risks

STRICT RULES:
- Return ONLY valid JSON
- No explanation, no markdown, no extra text
- Output format MUST be exactly:

{{
  "insights": ["insight 1", "insight 2", "insight 3"],
  "risks": ["risk 1", "risk 2", "risk 3"]
}}

Document:
{text}
"""

     
        # Call AI Service
        ai_response = call_groq(prompt)

        if not ai_response:
            return jsonify({
                "status": "error",
                "message": "AI service returned empty response"
            }), 500

       
        # Parse AI Response
        try:
            result = json.loads(ai_response)
        except json.JSONDecodeError:
            return jsonify({
                "status": "error",
                "message": "AI returned invalid JSON",
                "raw_output": ai_response
            }), 500

       
        # Validate Structure
        if not isinstance(result, dict):
            return jsonify({
                "status": "error",
                "message": "Invalid response format"
            }), 500

        if "insights" not in result or "risks" not in result:
            return jsonify({
                "status": "error",
                "message": "Missing required keys: insights, risks",
                "raw_output": ai_response
            }), 500

        if not isinstance(result["insights"], list) or not isinstance(result["risks"], list):
            return jsonify({
                "status": "error",
                "message": "insights and risks must be arrays"
            }), 500

        
        # Final Response
        
        return jsonify({
            "status": "success",
            "data": {
                "insights": result["insights"],
                "risks": result["risks"]
            }
        }), 200

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500