CREATE TABLE IF NOT EXISTS users(
    id SERIAL PRIMARY KEY,
    email_encrypted VARCHAR(500) NOT NULL,
    email_hash VARCHAR(100) NOT NULL,
    password VARCHAR(72) NOT NULL,
    username VARCHAR(60) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_app_user_email UNIQUE (email_hash),
    CONSTRAINT uk_app_user_username UNIQUE (username),
    CONSTRAINT ck_app_user_role CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

CREATE INDEX IF NOT EXISTS idx_email_hash ON users (email_hash);