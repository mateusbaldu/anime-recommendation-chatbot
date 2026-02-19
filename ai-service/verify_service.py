import requests
import json
import os
import time

BASE_URL = "http://localhost:8000"

def test_embed():
    print("Testing /embed endpoint...")
    url = f"{BASE_URL}/embed"
    payload = {"text": "This is a test sentence for embedding."}
    try:
        response = requests.post(url, json=payload)
        response.raise_for_status()
        data = response.json()
        if "embedding" in data and isinstance(data["embedding"], list):
            print(f"SUCCESS: Embedding received. Vector length: {len(data['embedding'])}")
        else:
            print(f"FAILURE: Unexpected response format: {data}")
    except Exception as e:
        print(f"FAILURE: Error calling /embed: {e}")

def test_chat():
    print("\nTesting /chat endpoint...")
    url = f"{BASE_URL}/chat"
    history = [
        {"role": "user", "content": "Hello, who are you?"}
    ]
    payload = {"history": history}
    try:
        response = requests.post(url, json=payload)
        if response.status_code == 500:
             print("WARNING: /chat endpoint returned 500. This might be due to missing GROQ_API_KEY.")
             print(f"Response: {response.text}")
        else:
             response.raise_for_status()
             data = response.json()
             if "response" in data:
                 print(f"SUCCESS: Chat response received: {data['response']}")
             else:
                 print(f"FAILURE: Unexpected response format: {data}")

    except Exception as e:
        print(f"FAILURE: Error calling /chat: {e}")

if __name__ == "__main__":
    print("Waiting for service to start...")
    try:
        health = requests.get(BASE_URL)
        if health.status_code == 200:
            print("Service is UP.")
            test_embed()
            test_chat()
        else:
            print(f"Service returned status {health.status_code}")
    except requests.exceptions.ConnectionError:
        print("ERROR: Could not connect to service. Is it running on port 8000?")
