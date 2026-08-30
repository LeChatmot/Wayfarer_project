CREATE TABLE IF NOT EXISTS refresh_tokens(
     id SERIAL PRIMARY KEY,
     token VARCHAR(255) NOT NULL,
     user_id INTEGER NOT NULL,
     expiry_date TIMESTAMPTZ NOT NULL,
     revoked BOOLEAN NOT NULL DEFAULT FALSE,
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     CONSTRAINT uk_refresh_token_token UNIQUE (token),
     CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_user_id ON refresh_tokens(user_id);