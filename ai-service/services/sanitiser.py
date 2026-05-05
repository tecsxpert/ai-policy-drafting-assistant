"""
Input Sanitisation Module — AI Developer 3
Blocks prompt injection, XSS, SQL injection, and other malicious inputs
before they reach the AI model.
"""

import re
import logging

logger = logging.getLogger(__name__)

# Prompt injection patterns — attackers use these to hijack AI behavior
INJECTION_PATTERNS = [
    r"ignore (all |previous )?instructions",
    r"disregard (all |previous )?instructions",
    r"you are now",
    r"act as",
    r"pretend (you are|to be)",
    r"forget (everything|all)",
    r"new persona",
    r"jailbreak",
    r"system prompt",
    r"override",
    r"bypass",
]

# XSS attack patterns
XSS_PATTERNS = [
    r"<script",
    r"javascript:",
    r"on\w+\s*=",          # onclick=, onerror=, etc.
    r"<iframe",
    r"<object",
    r"<embed",
]

# SQL injection patterns
SQL_PATTERNS = [
    r"'\s*OR\s*'1'\s*=\s*'1",
    r"DROP\s+TABLE",
    r";\s*--",
    r"UNION\s+SELECT",
    r"INSERT\s+INTO",
    r"DELETE\s+FROM",
]

# Maximum allowed input length
MAX_INPUT_LENGTH = 5000


def sanitise_input(text: str) -> dict:
    """
    Validates and sanitises user input before sending to the AI model.

    Returns:
        {"safe": True, "clean_text": "..."} if input is safe
        {"safe": False, "reason": "..."} if input is blocked
    """
    # Check for empty input
    if not text or not text.strip():
        return {"safe": False, "reason": "Input cannot be empty"}

    # Check input length
    if len(text) > MAX_INPUT_LENGTH:
        return {
            "safe": False,
            "reason": f"Input too long (max {MAX_INPUT_LENGTH} characters)"
        }

    # Check all security patterns BEFORE stripping HTML tags
    # (so <script> patterns are caught before removal)
    lower_text = text.lower()

    # Check for prompt injection patterns
    for pattern in INJECTION_PATTERNS:
        if re.search(pattern, lower_text, re.IGNORECASE):
            # Log detection without logging actual input content (PII safety)
            logger.warning(
                f"Prompt injection blocked: pattern='{pattern}', "
                f"input_length={len(text)} chars"
            )
            return {
                "safe": False,
                "reason": f"Input contains disallowed pattern"
            }

    # Check for XSS patterns (on ORIGINAL text, before tag stripping)
    for pattern in XSS_PATTERNS:
        if re.search(pattern, lower_text, re.IGNORECASE):
            logger.warning(
                f"XSS attack blocked: pattern='{pattern}', "
                f"input_length={len(text)} chars"
            )
            return {
                "safe": False,
                "reason": "Input contains potentially dangerous content"
            }

    # Check for SQL injection patterns
    for pattern in SQL_PATTERNS:
        if re.search(pattern, lower_text, re.IGNORECASE):
            logger.warning(
                f"SQL injection blocked: pattern='{pattern}', "
                f"input_length={len(text)} chars"
            )
            return {
                "safe": False,
                "reason": "Input contains disallowed pattern"
            }

    # All checks passed — now clean the text for safe output
    # Strip HTML tags
    clean_text = re.sub(r'<[^>]+>', '', text)

    # Remove newlines that could cause log injection
    clean_text = clean_text.replace('\n', ' ').replace('\r', ' ')

    # Safe — log input length only (never log actual content — PII policy)
    logger.info(f"Input sanitised successfully: {len(clean_text)} chars")

    return {"safe": True, "clean_text": clean_text}
