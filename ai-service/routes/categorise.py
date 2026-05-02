from flask import Blueprint, request, jsonify
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