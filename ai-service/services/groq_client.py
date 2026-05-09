import os
import time
import logging
import requests
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Logging setup
logging.basicConfig(level=logging.ERROR)


class GroqClient:
    def __init__(self):
        self.api_key = os.getenv("GROQ_API_KEY")
        self.url = os.getenv(
            "GROQ_URL",
            "https://api.groq.com/openai/v1/chat/completions"
        )

        # Default Groq-supported model
        self.model = os.getenv("GROQ_MODEL", "llama-3.1-8b-instant")

    def generate(self, prompt, model=None):
        """
        Generate response from Groq API
        """
        model = model or self.model

        if not self.api_key:
            return "Error: GROQ_API_KEY missing in .env"

        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }

        payload = {
            "model": model,
            "messages": [
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            "temperature": 0.3,
            "max_tokens": 2000
        }

        retries = 3

        for attempt in range(retries):
            try:
                print(f"[GroqClient] Attempt {attempt+1}")
                print("Using Model:", model)

                response = requests.post(
                    self.url,
                    json=payload,
                    headers=headers,
                    timeout=30
                )

                print("Status Code:", response.status_code)
                print("Response Text:", response.text)

                response.raise_for_status()

                data = response.json()

                return data["choices"][0]["message"]["content"]

            except Exception as e:
                logging.error(f"[GroqClient Error] Attempt {attempt+1}: {e}")

                if attempt < retries - 1:
                    time.sleep(2 ** attempt)

        return "AI service temporarily unavailable. Please try again later."


# ---- Global Config ----
GROQ_API_KEY = os.getenv("GROQ_API_KEY")

GROQ_URL = os.getenv(
    "GROQ_URL",
    "https://api.groq.com/openai/v1/chat/completions"
)

# Correct default model for Groq
DEFAULT_MODEL = os.getenv("GROQ_MODEL", "llama-3.1-8b-instant")


def call_groq(prompt: str, retries: int = 3, model: str = DEFAULT_MODEL):
    """
    Simple function-based Groq caller for routes
    """

    if not GROQ_API_KEY:
        return "Error: GROQ_API_KEY missing in .env"

    headers = {
        "Authorization": f"Bearer {GROQ_API_KEY}",
        "Content-Type": "application/json"
    }

    payload = {
        "model": model,
        "messages": [
            {
                "role": "user",
                "content": prompt
            }
        ],
        "temperature": 0.3,
        "max_tokens": 2000
    }

    for attempt in range(retries):
        try:
            print(f"[call_groq] Attempt {attempt+1}")
            print("Using Model:", model)
            print("API Key Present:", bool(GROQ_API_KEY))

            response = requests.post(
                GROQ_URL,
                json=payload,
                headers=headers,
                timeout=30
            )

            print("Groq Status Code:", response.status_code)
            print("Groq Response:", response.text)

            response.raise_for_status()

            data = response.json()

            return data["choices"][0]["message"]["content"]

        except Exception as e:
            logging.error(f"[Groq Error] Attempt {attempt+1}: {e}")

            if attempt < retries - 1:
                time.sleep(2 ** attempt)

    return "AI service temporarily unavailable. Please try again later."