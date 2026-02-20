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
from slowapi import Limiter, _rate_limit_exceeded_handler
from slowapi.util import get_remote_address
from slowapi.errors import RateLimitExceeded
from fastapi import Request

env_path = Path(__file__).parent.parent / '.env'
load_dotenv(dotenv_path=env_path)

limiter = Limiter(key_func=get_remote_address)
app = FastAPI(title="Anime Recommendation AI Service")
app.state.limiter = limiter
app.add_exception_handler(RateLimitExceeded, _rate_limit_exceeded_handler)

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
@limiter.limit("20/minute")
async def generate_embedding(request: Request, embed_req: EmbedRequest):
    if embedding_model is None:
        raise HTTPException(status_code=500, detail="Embedding model not initialized.")
    
    try:
        embedding = embedding_model.encode(embed_req.text)
        return {"embedding": embedding.tolist()}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating embedding: {str(e)}")

@app.post("/chat")
@limiter.limit("20/minute")
async def chat_with_llm(request: Request, chat_req: ChatRequest):
    try:
        chat = get_groq_chat()
        
        ROLE_TO_MESSAGE = {
            "user": HumanMessage,
            "assistant": AIMessage,
            "system": SystemMessage,
        }

        messages = [
            ROLE_TO_MESSAGE[msg["role"]](content=msg["content"])
            for msg in chat_req.history
            if msg.get("role") in ROLE_TO_MESSAGE
        ]
                
        response = chat.invoke(messages)
        return {"response": response.content}
        
    except Exception as e:
         raise HTTPException(status_code=500, detail=f"Error in chat processing: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
