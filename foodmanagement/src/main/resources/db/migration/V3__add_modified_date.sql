ALTER TABLE food ADD COLUMN modified_date DATETIME;
UPDATE food SET modified_date = created_date WHERE modified_date IS NULL; 