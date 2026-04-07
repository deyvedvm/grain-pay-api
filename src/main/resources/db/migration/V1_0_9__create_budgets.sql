CREATE TABLE budgets (
    id           BIGSERIAL     PRIMARY KEY,
    limit_amount NUMERIC(19,2) NOT NULL,
    month        INTEGER       NOT NULL,
    year         INTEGER       NOT NULL,
    category_id  BIGINT        NOT NULL REFERENCES categories (id),
    user_id      BIGINT        NOT NULL REFERENCES users (id),
    created_at   TIMESTAMP(6)  NOT NULL,
    updated_at   TIMESTAMP(6)  NOT NULL,
    CONSTRAINT uq_budget_user_category_month_year UNIQUE (user_id, category_id, month, year)
);
