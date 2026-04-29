import os
import time
 day2-day4
import logging
import requests
from dotenv import load_dotenv

load_dotenv()

logging.basicConfig(level=logging.ERROR)

def call_groq(prompt):
    api_key = os.getenv("GROQ_API_KEY")

    url = "https://api.groq.com/openai/v1/chat/completions"

    headers = {
        "Authorization": f"Bearer {api_key}",

import requests

GROQ_API_KEY = os.getenv("GROQ_API_KEY")
GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"
MODEL = "llama-3.3-70b-versatile"


def call_groq(prompt: str, retries: int = 3):
    headers = {
        "Authorization": f"Bearer {GROQ_API_KEY}",
 main
        "Content-Type": "application/json"
    }

    payload = {
 day2-day4
        "model": "llama-3.1-8b-instant",
        "messages": [
            {"role": "user", "content": prompt}
        ]
    }

    for attempt in range(3):
        try:
            response = requests.post(url, headers=headers, json=payload)

            if response.status_code == 200:
                data = response.json()
                return data["choices"][0]["message"]["content"]

        except Exception as e:
            logging.error(str(e))

        time.sleep(2 ** attempt)

    return "Failed after retries"

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
