CREATE TABLE goals (
    id             BIGSERIAL      PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    target_amount  NUMERIC(19,2)  NOT NULL,
    current_amount NUMERIC(19,2)  NOT NULL DEFAULT 0,
    deadline       DATE           NOT NULL,
    description    VARCHAR(500),
    priority       VARCHAR(50)    NOT NULL,
    status         VARCHAR(50)    NOT NULL DEFAULT 'ACTIVE',
    user_id        BIGINT         NOT NULL REFERENCES users (id),
    created_at     TIMESTAMP(6)   NOT NULL,
    updated_at     TIMESTAMP(6)   NOT NULL
);
