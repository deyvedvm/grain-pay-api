create table expense
(
    id           bigserial primary key,
    amount       numeric(19, 2) not null,
    created_at   timestamp(6)   not null,
    date         timestamp(6)   not null,
    description  varchar(255)   not null,
    payment_type varchar(255)   not null,
    updated_at   timestamp(6)
);
