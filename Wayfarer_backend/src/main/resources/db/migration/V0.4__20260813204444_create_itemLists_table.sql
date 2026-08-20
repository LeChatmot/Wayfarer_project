CREATE TABLE IF NOT EXISTS items_lists(
  id SERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  user_id BIGSERIAL NOT NULL,
  CONSTRAINT fk_user
      FOREIGN KEY(user_id)
          REFERENCES users(id)
);
