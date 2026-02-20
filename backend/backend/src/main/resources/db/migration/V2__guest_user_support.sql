-- V2: Add guest user support
-- Relax NOT NULL constraints so guest users can exist without Google credentials

ALTER TABLE users ALTER COLUMN google_sub DROP NOT NULL;
ALTER TABLE users ALTER COLUMN email DROP NOT NULL;

ALTER TABLE users ADD COLUMN is_guest BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN guest_session_id TEXT UNIQUE;
