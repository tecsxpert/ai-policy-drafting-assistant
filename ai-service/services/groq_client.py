import os
import time
 features
import logging
import requests
from dotenv import load_dotenv

load_dotenv()

logging.basicConfig(level=logging.ERROR)

class GroqClient:
    def _init_(self):
        self.api_key = os.getenv("GROQ_API_KEY")
        self.url = "https://api.groq.com/openai/v1/chat/completions"

    def generate(self, prompt):
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }

        payload = {
            "model": "llama3-70b-8192",
            "messages": [
                {"role": "user", "content": prompt}
            ]
        }

        retries = 3

        for attempt in range(retries):
            try:
                response = requests.post(
                    self.url,
                    json=payload,
                    headers=headers,
                    timeout=30
                )

                response.raise_for_status()

                data = response.json()

                return data["choices"][0]["message"]["content"]

            except Exception as e:
                logging.error(f"Attempt {attempt+1} Failed: {e}")

                if attempt < retries - 1:
                    time.sleep(2 ** attempt)

        return "Error generating response"

import requests

GROQ_API_KEY = os.getenv("GROQ_API_KEY")
GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"
MODEL = "llama-3.3-70b-versatile"


def call_groq(prompt: str, retries: int = 3):
    headers = {
        "Authorization": f"Bearer {GROQ_API_KEY}",
        "Content-Type": "application/json"
    }

    payload = {
        "model": MODEL,
        "messages": [
            {"role": "user", "content": prompt}
        ],
        "temperature": 0.3,
        "max_tokens": 2000
    }

    for attempt in range(retries):
        try:
            response = requests.post(GROQ_URL, json=payload, headers=headers)

            if response.status_code == 200:
                return response.json()["choices"][0]["message"]["content"]

        except Exception as e:
            print(f"[Groq Error] Attempt {attempt+1}: {e}")

        time.sleep(2 ** attempt)

    # fallback (MANDATORY per spec)
    return "AI service temporarily unavailable. Please try again later."
main
