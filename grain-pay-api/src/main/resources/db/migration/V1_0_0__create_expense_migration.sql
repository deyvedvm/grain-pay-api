CREATE TABLE expense (
    id           bigint         not null constraint expense_pkey primary key,
    created_at   timestamp      not null,
    date         timestamp      not null,
    description  varchar(255)   not null,
    external_id  uuid,
    payment_type varchar(255)   not null,
    updated_at   timestamp,
    value        numeric(19, 2) not null
);