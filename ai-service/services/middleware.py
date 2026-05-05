"""
Sanitisation Middleware — AI Developer 3
Flask decorator that automatically validates and sanitises request input
before it reaches route handlers.
"""

from flask import request, jsonify
from services.sanitiser import sanitise_input
import functools


def sanitise_request(f):
    """
    Decorator for Flask routes — checks input safety before processing.

    Usage:
        @app.route("/endpoint", methods=["POST"])
        @sanitise_request
        def my_endpoint():
            ...

    Behavior:
        - Requires JSON request body
        - Checks 'text', 'input', or 'content' fields for malicious content
        - Returns 400 with error details if unsafe
        - Replaces original text with cleaned version if safe
    """
    @functools.wraps(f)
    def decorated_function(*args, **kwargs):
        data = request.get_json(silent=True)

        if not data:
            return jsonify({
                "error": "Request body must be JSON",
                "details": "Content-Type must be application/json with valid JSON body"
            }), 400

        # Find the text field — routes use different field names
        text_to_check = (
            data.get('text') or
            data.get('input') or
            data.get('content') or
            ''
        )

        # For batch-process, check items instead
        if 'items' in data and isinstance(data['items'], list):
            for item in data['items']:
                if isinstance(item, str):
                    result = sanitise_input(item)
                    if not result["safe"]:
                        return jsonify({
                            "error": "Input validation failed",
                            "reason": result["reason"]
                        }), 400
            return f(*args, **kwargs)

        # Validate the text field
        if text_to_check:
            result = sanitise_input(text_to_check)

            if not result["safe"]:
                return jsonify({
                    "error": "Input validation failed",
                    "reason": result["reason"]
                }), 400

            # Replace original text with cleaned version
            if 'clean_text' in result:
                if 'text' in data:
                    data['text'] = result['clean_text']
                if 'input' in data:
                    data['input'] = result['clean_text']
                if 'content' in data:
                    data['content'] = result['clean_text']

        return f(*args, **kwargs)

    return decorated_function
