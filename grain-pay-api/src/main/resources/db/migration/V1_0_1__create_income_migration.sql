CREATE TABLE income
(
    id          SERIAL PRIMARY KEY,
    description TEXT           NOT NULL,
    amount      NUMERIC(10, 2) NOT NULL,
    date        DATE           NOT NULL
);