import os
import time
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