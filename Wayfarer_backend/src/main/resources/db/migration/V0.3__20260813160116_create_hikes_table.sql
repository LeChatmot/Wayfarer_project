CREATE TABLE IF NOT EXISTS hikes (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(60) NOT NULL,
     id_creator BIGINT NOT NULL,
     starting_point GEOMETRY(POINT, 4326) NOT NULL,
     back_to_start BOOLEAN NOT NULL DEFAULT false,
     id_document_mongo BIGINT,
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_hikes_starting_point ON hikes USING GIST(starting_point);