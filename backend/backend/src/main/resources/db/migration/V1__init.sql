CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE data_sources (
  id          SERIAL PRIMARY KEY,
  name        TEXT NOT NULL UNIQUE,
  description TEXT
);

INSERT INTO data_sources (name, description) VALUES
  ('letterboxd', 'Importado via CSV do Letterboxd'),
  ('mal',        'Importado via CSV do MyAnimeList'),
  ('imdb',       'Importado via CSV do IMDB'),
  ('manual',     'Inserido manualmente pelo usuário');


CREATE TABLE users (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  google_sub      TEXT UNIQUE NOT NULL,
  email           TEXT UNIQUE NOT NULL,
  display_name    TEXT,
  profile_image   TEXT,
  persona_summary TEXT, 
  created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
  consented_at    TIMESTAMP,        
  deleted_at      TIMESTAMP         
);


CREATE TABLE works (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mal_id           INTEGER UNIQUE,   
  source_id        INTEGER REFERENCES data_sources(id),
  title            TEXT NOT NULL,
  title_english    TEXT,
  synopsis         TEXT,
  genres           TEXT[],
  themes           TEXT[],
  media_type       TEXT CHECK (media_type IN ('anime', 'movie', 'series', 'manga')),
  popularity_count INTEGER,          
  external_score   NUMERIC(4,2),     
  embedding        VECTOR(384),      
  created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX works_embedding_idx ON works USING hnsw (embedding vector_cosine_ops);

CREATE INDEX works_mal_id_idx ON works(mal_id);


CREATE TABLE user_reviews (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  work_id          UUID REFERENCES works(id),
  external_title   TEXT,             
  source_id        INTEGER REFERENCES data_sources(id),
  normalized_score NUMERIC(5,2) CHECK (normalized_score >= 0 AND normalized_score <= 10),
  review_text      TEXT,
  reviewed_at      TIMESTAMP,        
  created_at       TIMESTAMP NOT NULL DEFAULT NOW(),

  CONSTRAINT review_must_have_work CHECK (work_id IS NOT NULL OR external_title IS NOT NULL)
);

CREATE INDEX user_reviews_user_id_idx ON user_reviews(user_id);


CREATE TABLE user_work_status (
  user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  work_id    UUID NOT NULL REFERENCES works(id),
  status     TEXT NOT NULL CHECK (status IN ('completed', 'watching', 'dropped', 'plan_to_watch')),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

  PRIMARY KEY (user_id, work_id) 
);


CREATE TABLE chat_sessions (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  messages   JSONB NOT NULL DEFAULT '[]', 
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX chat_sessions_user_id_idx ON chat_sessions(user_id);


CREATE TABLE recommendations (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  work_id        UUID NOT NULL REFERENCES works(id),
  chat_session_id UUID REFERENCES chat_sessions(id), 
  reason         TEXT,                               
  diversity_flag BOOLEAN DEFAULT FALSE,              
  created_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX recommendations_user_id_idx ON recommendations(user_id);
