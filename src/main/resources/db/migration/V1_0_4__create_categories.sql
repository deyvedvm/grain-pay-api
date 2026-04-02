CREATE TABLE categories (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    icon       VARCHAR(100),
    color      VARCHAR(50),
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL
);
