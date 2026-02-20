# Local Setup Guide

Para executar a aplicação localmente e subir o `docker-compose`, é necessário configurar as variáveis de ambiente das três partes do projeto (Frontend, Backend e AI Service).

## Passo a Passo

1. **Frontend:**
   Vá até a pasta `frontend` e renomeie o arquivo `.env.example` para `.env`.
   Verifique se a `VITE_API_URL` está apontando para o seu backend.

2. **Backend:**
   Vá até a pasta `backend/backend` e renomeie o arquivo `.env.example` para `.env`.
   Substitua os seguintes valores:
   - `DB_PASSWORD`: A senha que será usada para criar e conectar com a base de dados Postgres.
   - `JWT_SECRET`: Uma chave secreta longa usada pelo Spring Security interno.

3. **AI Service:**
   Vá até a pasta `ai-service` e renomeie o arquivo `.env.example` para `.env`.
   Substitua as seguintes chaves com suas respectivas APIs reais:
   - `GROQ_API_KEY`: Usada para inferência via Groq.
   - `GEMINI_API_KEY`: Usada para inferência via Gemini.

4. **Executando a Aplicação:**
   Com todos os arquivos `.env` preenchidos, retorne para a raiz do repositório e execute o comando:
   ```bash
   docker-compose up -d --build
   ```
