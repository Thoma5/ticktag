BEGIN;

CREATE TABLE IF NOT EXISTS "user" (
  "id"            UUID PRIMARY KEY,
  "mail"          TEXT NOT NULL,
  "name"          TEXT NOT NULL,
  "password_hash" TEXT NOT NULL,
  "role"          TEXT NOT NULL,
  "current_token" UUID NOT NULL
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
  "u_id"         UUID      NOT NULL REFERENCES "user",
  "p_id"         UUID      NOT NULL REFERENCES "project",
  "project_role" TEXT      NOT NULL,
  "join_date"    TIMESTAMP NOT NULL,
  PRIMARY KEY (u_id, p_id)
);
CREATE INDEX ON "member" ("p_id");

CREATE TABLE IF NOT EXISTS "ticket" (
  "id"                     UUID PRIMARY KEY,
  "number"                 INTEGER     NOT NULL,
  "parent_ticket_id"       UUID        NULL REFERENCES "ticket",
  "project_id"             UUID        NOT NULL REFERENCES "project",
  "created_by"             UUID        NOT NULL REFERENCES "user",
  "description_comment_id" UUID UNIQUE NOT NULL, -- REFERENCES "comment", added below due to circular references
  "create_time"            TIMESTAMP   NOT NULL,
  "title"                  TEXT        NOT NULL,
  "open"                   BOOLEAN     NOT NULL,
  "story_points"           INTEGER,
  "initial_estimated_time" BIGINT,
  "current_estimated_time" BIGINT,
  "due_date"               TIMESTAMP
);
CREATE INDEX ON "ticket" ("number");
CREATE INDEX ON "ticket" ("parent_ticket_id");
CREATE INDEX ON "ticket" ("project_id");
CREATE INDEX ON "ticket" ("created_by");

CREATE TABLE IF NOT EXISTS "comment" (
  "id"          UUID PRIMARY KEY,
  "user_id"     UUID      NOT NULL REFERENCES "user",
  "ticket_id"   UUID      NOT NULL REFERENCES "ticket",
  "create_time" TIMESTAMP NOT NULL,
  "text"        TEXT      NOT NULL
);
CREATE INDEX ON "comment" ("user_id");
CREATE INDEX ON "comment" ("ticket_id");

-- Circular comment reference resolved here
ALTER TABLE "ticket"
  ADD FOREIGN KEY ("description_comment_id") REFERENCES "comment";

CREATE TABLE IF NOT EXISTS "ticket_tag" (
  "id"         UUID PRIMARY KEY,
  "project_id" UUID REFERENCES "project",
  "group_id"   UUID NOT NULL,
  "name"       TEXT NOT NULL,
  "color"      TEXT NOT NULL -- RRGGBB
);
CREATE INDEX ON "ticket_tag" ("project_id");
CREATE INDEX ON "ticket_tag" ("group_id");

CREATE TABLE IF NOT EXISTS "assignment_tag" (
  "id"         UUID PRIMARY KEY,
  "project_id" UUID REFERENCES "project",
  "name"       TEXT NOT NULL,
  "color"      TEXT NOT NULL -- RRGGBB
);
CREATE INDEX ON "assignment_tag" ("project_id");

CREATE TABLE IF NOT EXISTS "time_category" (
  "id"         UUID PRIMARY KEY,
  "project_id" UUID REFERENCES "project",
  "name"       TEXT NOT NULL
);
CREATE INDEX ON "time_category" ("project_id");

CREATE TABLE IF NOT EXISTS "assigned_ticket_tag" (
  "ticket_id"     UUID REFERENCES "ticket",
  "ticket_tag_id" UUID REFERENCES "ticket_tag",
  PRIMARY KEY ("ticket_id", "ticket_tag_id")
);
CREATE INDEX ON "assigned_ticket_tag" ("ticket_tag_id");

CREATE TABLE IF NOT EXISTS "assigned_ticket_user" (
  "ticket_id"         UUID REFERENCES "ticket",
  "assignment_tag_id" UUID REFERENCES "assignment_tag",
  "user_id"           UUID REFERENCES "user",
  PRIMARY KEY ("ticket_id", "assignment_tag_id", "user_id")
);
CREATE INDEX ON "assigned_ticket_user" ("assignment_tag_id");
CREATE INDEX ON "assigned_ticket_user" ("user_id");

CREATE TABLE IF NOT EXISTS "logged_time" (
  "id"          UUID PRIMARY KEY,
  "comment_id"  UUID    NOT NULL REFERENCES "comment",
  "category_id" UUID    NOT NULL REFERENCES "time_category",
  "time"        BIGINT NOT NULL
);
CREATE INDEX ON "logged_time" ("comment_id");
CREATE INDEX ON "logged_time" ("category_id");

CREATE TABLE IF NOT EXISTS "mentioned_ticket" (
  "comment_id"          UUID REFERENCES "comment",
  "mentioned_ticket_id" UUID REFERENCES "ticket",
  PRIMARY KEY ("comment_id", "mentioned_ticket_id")
);
CREATE INDEX ON "mentioned_ticket" ("mentioned_ticket_id");

COMMIT;
