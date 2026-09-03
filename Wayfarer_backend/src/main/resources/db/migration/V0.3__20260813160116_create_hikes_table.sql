CREATE TABLE IF NOT EXISTS hikes (
     id SERIAL PRIMARY KEY,
     name VARCHAR(60) NOT NULL,
     id_creator SERIAL,
     starting_point GEOMETRY(POINTZ, 4326) NOT NULL,
     back_to_start BOOLEAN NOT NULL DEFAULT false,
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     description TEXT,
     path GEOMETRY(LINESTRINGZ, 4326) NOT NULL DEFAULT 'LINESTRINGZ EMPTY',
     distance_meters DOUBLE PRECISION NOT NULL DEFAULT 0,
     elevation_gain DOUBLE PRECISION NOT NULL DEFAULT 0,
     elevation_loss DOUBLE PRECISION NOT NULL DEFAULT 0,
     duration_seconds INTEGER,
     difficulty VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
     CONSTRAINT fk_user
         FOREIGN KEY(id_creator)
             REFERENCES users(id)
);
ALTER TABLE hikes ALTER COLUMN path DROP DEFAULT;

CREATE INDEX IF NOT EXISTS idx_hikes_starting_point ON hikes USING GIST(starting_point);
CREATE INDEX IF NOT EXISTS idx_hikes_path ON hikes USING GIST(path);
CREATE INDEX IF NOT EXISTS idx_hikes_creator ON hikes(id_creator);

CREATE TABLE IF NOT EXISTS favorites (
     user_id BIGINT NOT NULL,
     hike_id BIGINT NOT NULL,
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     PRIMARY KEY (user_id, hike_id),
     CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
     CONSTRAINT fk_favorite_hike FOREIGN KEY (hike_id) REFERENCES hikes(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_favorites_hike ON favorites(hike_id);