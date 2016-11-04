begin;

create table "user" (
    "id" uuid primary key,
    "mail" text not null,
    "name" text not null,
    "password_hash" text not null,
    "role" text not null,
    "current_token" uuid not null
);
create index on "user" (upper("mail"));

commit;
