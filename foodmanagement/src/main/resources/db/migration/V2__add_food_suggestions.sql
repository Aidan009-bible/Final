CREATE TABLE food_suggestion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    food_id INT NOT NULL,
    suggested_food_id INT NOT NULL,
    purchase_count INT DEFAULT 0,
    last_purchased DATETIME,
    FOREIGN KEY (food_id) REFERENCES food(id),
    FOREIGN KEY (suggested_food_id) REFERENCES food(id)
);

CREATE INDEX idx_food_suggestion_food_id ON food_suggestion(food_id);
CREATE INDEX idx_food_suggestion_counts ON food_suggestion(food_id, purchase_count DESC); 