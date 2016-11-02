begin;

create table "user" (
    "id" uuid primary key,
    "mail" text not null,
    "password_hash" text not null
);

commit;
