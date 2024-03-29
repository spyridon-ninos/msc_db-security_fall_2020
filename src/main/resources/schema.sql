CREATE TABLE IF NOT EXISTS shootings (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128),
    date TIMESTAMP,
    manner_of_death VARCHAR(64),
    armed VARCHAR(64),
    age DECIMAL,
    gender CHAR,
    race VARCHAR(64),
    city VARCHAR(64),
    state VARCHAR(8),
    signs_of_mental_illness BOOLEAN,
    threat_level VARCHAR(64),
    flee VARCHAR(64),
    body_camera BOOLEAN,
    arms_category VARCHAR(64)
);