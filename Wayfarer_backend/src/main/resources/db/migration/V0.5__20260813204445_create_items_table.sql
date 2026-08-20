CREATE TABLE IF NOT EXISTS items(
    id SERIAL PRIMARY KEY,
    item_list_id SERIAL NOT NULL,
    name VARCHAR(60) NOT NULL,
    quantity INTEGER,
    CONSTRAINT fk_item_list
        FOREIGN KEY(item_list_id)
            REFERENCES items_lists(id)
);
