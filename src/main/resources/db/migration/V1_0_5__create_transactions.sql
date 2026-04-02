CREATE TABLE transactions (
    id                  BIGSERIAL     PRIMARY KEY,
    type                VARCHAR(50)   NOT NULL,
    amount              NUMERIC(19,2) NOT NULL,
    date                DATE          NOT NULL,
    description         VARCHAR(255)  NOT NULL,
    payment_type        VARCHAR(50),
    notes               TEXT,
    category_id         BIGINT        REFERENCES categories (id),
    user_id             BIGINT        NOT NULL REFERENCES users (id),
    installments        INTEGER,
    current_installment INTEGER,
    is_recurring        BOOLEAN       DEFAULT FALSE,
    source              VARCHAR(50),
    created_at          TIMESTAMP(6)  NOT NULL,
    updated_at          TIMESTAMP(6)  NOT NULL
);

CREATE TABLE transaction_tags (
    transaction_id BIGINT       NOT NULL REFERENCES transactions (id) ON DELETE CASCADE,
    tag            VARCHAR(100) NOT NULL
);
