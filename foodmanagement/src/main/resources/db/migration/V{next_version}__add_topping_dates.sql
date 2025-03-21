ALTER TABLE topping 
ADD COLUMN created_date DATETIME,
ADD COLUMN modified_date DATETIME;

UPDATE topping 
SET created_date = NOW(), 
    modified_date = NOW() 
WHERE created_date IS NULL; 