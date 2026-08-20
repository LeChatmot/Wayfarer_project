CREATE TABLE IF NOT EXISTS hikes (
     id SERIAL PRIMARY KEY,
     name VARCHAR(60) NOT NULL,
     id_creator BIGINT,
     starting_point GEOMETRY(POINT, 4326) NOT NULL,
     back_to_start BOOLEAN NOT NULL DEFAULT false,
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     CONSTRAINT fk_user
         FOREIGN KEY(id_creator)
             REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_hikes_starting_point ON hikes USING GIST(starting_point);