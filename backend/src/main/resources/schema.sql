BEGIN;

CREATE TABLE IF NOT EXISTS "user" (
  "id"            UUID PRIMARY KEY,
  "mail"          TEXT NOT NULL,
  "name"          TEXT NOT NULL,
  "password_hash" TEXT NOT NULL,
  "role"          TEXT NOT NULL,
  "current_token" UUID NOT NULL,
  "profile_pic"   BYTEA
);
CREATE INDEX ON "user" (upper("mail"));

CREATE TABLE IF NOT EXISTS "project" (
  "id"            UUID PRIMARY KEY,
  "name"          TEXT      NOT NULL,
  "description"   TEXT      NOT NULL,
  "creation_date" TIMESTAMP NOT NULL,
  "icon"          BYTEA
);
CREATE TABLE IF NOT EXISTS "member" (
  "u_id"         UUID      NOT NULL REFERENCES "user" (id),
  "p_id"         UUID      NOT NULL REFERENCES "project" (id),
  "project_role" TEXT      NOT NULL,
  "join_date"    TIMESTAMP NOT NULL,
  PRIMARY KEY (u_id, p_id)
);

COMMIT;
