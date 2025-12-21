-- liquibase formatted sql logicalFilePath: db/changelog/ddl/badminton/user.sql

-- changeset maksmolch:create_user_table
create table if not exists badminton.user (
    id uuid primary key,
    tg_id int not null,
    created_at timestamp not null,
    first_name varchar(255),
    last_name varchar(255),
    photo_url varchar(255),
    username varchar(255)
);

-- changeset maksmolch:create_unique_index_on_user_tg_id
create unique index if not exists idx_user_tg_id on badminton.user (tg_id);
