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

CREATE TABLE "project" (
    "id"            UUID PRIMARY KEY,
    "name"          TEXT NOT NULL,
    "description"   TEXT NOT NULL,
    "creation_date" TIMESTAMP NOT NULL,
    "icon"          BYTEA
);

commit;
