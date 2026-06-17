-- Seed categories into local development
-- add 4 new rows into Categories table
-- each line is a new row with following values: category name, category description, timestamp when category is created
-- note: id is not included since MySQL auto-generates it
-- Seed basic categories for local development
INSERT INTO categories (name, description, created_at)
VALUES
    ('Electronics', 'Phones, laptops, accessories, and gadgets', NOW()),
    ('Books', 'Books, textbooks, and educational materials', NOW()),
    ('Clothing', 'Fashion, shoes, and accessories', NOW()),
    ('Home', 'Home goods, kitchen, and furniture', NOW())
ON DUPLICATE KEY UPDATE
    -- allow updating description in the future
    description = VALUES(description);