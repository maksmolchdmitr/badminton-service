-- liquibase formatted sql logicalFilePath: db/changelog/ddl/badminton/user_table.sql

-- changeset maksmolch:create_user_table_table
create table if not exists badminton.user_table (
    id uuid primary key,
    tg_id bigint not null,
    created_at timestamp not null,
    first_name varchar(255),
    last_name varchar(255),
    photo_url varchar(255),
    username varchar(255)
);

-- changeset maksmolch:create_unique_index_on_user_table_tg_id
create unique index if not exists idx_user_table_tg_id on badminton.user_table (tg_id);
