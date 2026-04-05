CREATE TABLE recurring_transactions
(
    id               BIGSERIAL PRIMARY KEY,
    description      VARCHAR(255)   NOT NULL,
    amount           NUMERIC(19, 2) NOT NULL,
    type             VARCHAR(50)    NOT NULL,
    payment_type     VARCHAR(50),
    category_id      BIGINT REFERENCES categories (id) ON DELETE SET NULL,
    account_id       BIGINT REFERENCES accounts (id) ON DELETE SET NULL,
    recurrence_type  VARCHAR(50)    NOT NULL,
    start_date       DATE           NOT NULL,
    end_date         DATE,
    day_of_month     INTEGER,
    is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
    user_id          BIGINT         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    created_by       VARCHAR(255),
    last_modified_by VARCHAR(255)
);
