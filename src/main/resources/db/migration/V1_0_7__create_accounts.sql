CREATE TABLE accounts (
    id         BIGSERIAL     PRIMARY KEY,
    name       VARCHAR(255)  NOT NULL,
    type       VARCHAR(50)   NOT NULL,
    bank_name  VARCHAR(255),
    balance    NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    user_id    BIGINT        NOT NULL REFERENCES users (id),
    created_at TIMESTAMP(6)  NOT NULL,
    updated_at TIMESTAMP(6)  NOT NULL
);

ALTER TABLE transactions
    ADD COLUMN account_id BIGINT REFERENCES accounts (id);
