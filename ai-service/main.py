from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
from langchain_groq import ChatGroq
from langchain_core.messages import HumanMessage, SystemMessage, AIMessage
from langchain_core.prompts import ChatPromptTemplate
from dotenv import load_dotenv
import os
from typing import List, Dict, Any
from pathlib import Path

env_path = Path(__file__).parent.parent / '.env'
load_dotenv(dotenv_path=env_path)

app = FastAPI(title="Anime Recommendation AI Service")

try:
    embedding_model = SentenceTransformer('all-MiniLM-L6-v2')
except Exception as e:
    embedding_model = None

GROQ_API_KEY = os.getenv("GROQ_API_KEY")

def get_groq_chat():
    if not GROQ_API_KEY:
        raise HTTPException(status_code=500, detail="GROQ_API_KEY not configured.")
    return ChatGroq(temperature=0, model_name="llama3-8b-8192", groq_api_key=GROQ_API_KEY)

class EmbedRequest(BaseModel):
    text: str

class ChatRequest(BaseModel):
    history: List[Dict[str, str]]

@app.get("/")
async def root():
    return {"message": "Anime Recommendation AI Service is running"}

@app.post("/embed")
async def generate_embedding(request: EmbedRequest):
    if embedding_model is None:
        raise HTTPException(status_code=500, detail="Embedding model not initialized.")
    
    try:
        embedding = embedding_model.encode(request.text)
        return {"embedding": embedding.tolist()}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating embedding: {str(e)}")

@app.post("/chat")
async def chat_with_llm(request: ChatRequest):
    try:
        chat = get_groq_chat()
        
        messages = []
        for msg in request.history:
            role = msg.get("role")
            content = msg.get("content")
            if role == "user":
                messages.append(HumanMessage(content=content))
            elif role == "assistant":
                messages.append(AIMessage(content=content))
            elif role == "system":
                 messages.append(SystemMessage(content=content))
                
        response = chat.invoke(messages)
        return {"response": response.content}
        
    except Exception as e:
         raise HTTPException(status_code=500, detail=f"Error in chat processing: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
