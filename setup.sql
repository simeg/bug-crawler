-- # Start container:
-- docker-compose up

-- # Connect to running container
-- psql -h localhost -p 5432 -U postgres -W postgres

CREATE DATABASE web_crawler;

CREATE TABLE bug (
  id SERIAL PRIMARY KEY,
  type VARCHAR(80) NOT NULL,
  base_url VARCHAR(2000) NOT NULL,
  path VARCHAR(2000),
  description VARCHAR(1000),
  time_inserted TIMESTAMP NOT NULL
);
