-- liquibase formatted sql logicalFilePath: db/changelog/ddl/badminton/refresh_token.sql

-- changeset maksmolch:create_refresh_token_table
create table if not exists badminton.refresh_token (
    token uuid primary key,
    user_id uuid not null,
    created_at timestamp not null,
    expires_at timestamp not null,
    constraint refresh_token_user_id_foreign_key foreign key (user_id) references badminton.user_table (id),
    constraint refresh_token_user_id_unique unique (user_id)
);
