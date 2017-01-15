BEGIN;

CREATE TABLE IF NOT EXISTS "user" (
  "id"            UUID PRIMARY KEY,
  "username"      TEXT NOT NULL,
  "mail"          TEXT NOT NULL,
  "name"          TEXT NOT NULL,
  "password_hash" TEXT NOT NULL,
  "role"          TEXT NOT NULL,
  "current_token" UUID NOT NULL,
  "disabled"      BOOLEAN
);
CREATE INDEX ON "user" (upper("mail"));

CREATE TABLE IF NOT EXISTS "user_image" (
  "user_id" UUID PRIMARY KEY REFERENCES "user",
  "image"   BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS "project" (
  "id"             UUID PRIMARY KEY,
  "name"           TEXT      NOT NULL,
  "description"    TEXT      NOT NULL,
  "creation_date"  TIMESTAMP NOT NULL,
  "icon_mime_info" TEXT,
  "icon"           BYTEA,
  "disabled"       BOOLEAN   NOT NULL
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
  "description_comment_id" UUID UNIQUE NULL ,  -- REFERENCES "comment", added below due to circular references
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
  ADD FOREIGN KEY ("description_comment_id") REFERENCES "comment" ;


CREATE TABLE IF NOT EXISTS "ticket_tag_group" (
  "id"                    UUID PRIMARY KEY,
  "project_id"            UUID REFERENCES "project",
  "default_ticket_tag_id" UUID    NULL,
  "name"                  TEXT    NOT NULL,
  "exclusive"             BOOLEAN NOT NULL
);
CREATE INDEX ON "ticket_tag_group" ("project_id");

CREATE TABLE IF NOT EXISTS "ticket_tag" (
  "id"                  UUID PRIMARY KEY,
  "ticket_tag_group_id" UUID REFERENCES "ticket_tag_group",
  "name"                TEXT    NOT NULL,
  "normalized_name"     TEXT    NOT NULL,
  "color"               TEXT    NOT NULL, -- RRGGBB
  "order"               INTEGER NOT NULL
);
CREATE INDEX ON "ticket_tag" ("ticket_tag_group_id");

-- Circular comment reference resolved here
ALTER TABLE "ticket_tag_group"
  ADD FOREIGN KEY ("default_ticket_tag_id") REFERENCES "ticket_tag";

CREATE TABLE IF NOT EXISTS "assignment_tag" (
  "id"              UUID PRIMARY KEY,
  "project_id"      UUID REFERENCES "project",
  "name"            TEXT NOT NULL,
  "normalized_name" TEXT NOT NULL,
  "color"           TEXT NOT NULL -- RRGGBB
);
CREATE INDEX ON "assignment_tag" ("project_id");

CREATE TABLE IF NOT EXISTS "time_category" (
  "id"              UUID PRIMARY KEY,
  "project_id"      UUID REFERENCES "project",
  "name"            TEXT NOT NULL,
  "normalized_name" TEXT NOT NULL
);
CREATE INDEX ON "time_category" ("project_id");

CREATE TABLE IF NOT EXISTS "assigned_ticket_tag" (
  "ticket_id"     UUID REFERENCES "ticket" ON DELETE CASCADE,
  "ticket_tag_id" UUID REFERENCES "ticket_tag" ON DELETE CASCADE,
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
  "time"        BIGINT  NOT NULL,
  "canceled"    BOOLEAN NOT NULL
);
CREATE INDEX ON "logged_time" ("comment_id");
CREATE INDEX ON "logged_time" ("category_id");

CREATE TABLE IF NOT EXISTS "mentioned_ticket" (
  "comment_id"          UUID REFERENCES "comment" ON DELETE CASCADE,
  "mentioned_ticket_id" UUID REFERENCES "ticket" ON DELETE CASCADE,
  PRIMARY KEY ("comment_id", "mentioned_ticket_id")
);
CREATE INDEX ON "mentioned_ticket" ("mentioned_ticket_id");

CREATE TABLE "ticket_event" (
  "id"        UUID PRIMARY KEY,
  "ticket_id" UUID      NOT NULL REFERENCES "ticket",
  "user_id"   UUID      NOT NULL REFERENCES "user",
  "time"      TIMESTAMP NOT NULL
);
CREATE INDEX ON "ticket_event" ("ticket_id");
CREATE INDEX ON "ticket_event" ("user_id");

CREATE TABLE IF NOT EXISTS "ticket_event_parent_changed" (
  "id"            UUID PRIMARY KEY REFERENCES "ticket_event",
  "src_parent_id" UUID REFERENCES "ticket",
  "dst_parent_id" UUID REFERENCES "ticket"
);
CREATE INDEX ON "ticket_event_parent_changed" ("src_parent_id");
CREATE INDEX ON "ticket_event_parent_changed" ("dst_parent_id");

CREATE TABLE IF NOT EXISTS "ticket_event_title_changed" (
  "id"        UUID PRIMARY KEY REFERENCES "ticket_event",
  "src_title" TEXT NOT NULL,
  "dst_title" TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS "ticket_event_state_changed" (
  "id"        UUID PRIMARY KEY REFERENCES "ticket_event",
  "src_state" BOOLEAN NOT NULL,
  "dst_state" BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS "ticket_event_story_points_changed" (
  "id"               UUID PRIMARY KEY REFERENCES "ticket_event",
  "src_story_points" INTEGER,
  "dst_story_points" INTEGER
);

CREATE TABLE IF NOT EXISTS "ticket_event_initial_estimated_time_changed" (
  "id"                         UUID PRIMARY KEY REFERENCES "ticket_event",
  "src_initial_estimated_time" BIGINT,
  "dst_initial_estimated_time" BIGINT
);

CREATE TABLE IF NOT EXISTS "ticket_event_current_estimated_time_changed" (
  "id"                         UUID PRIMARY KEY REFERENCES "ticket_event",
  "src_current_estimated_time" BIGINT,
  "dst_current_estimated_time" BIGINT
);

CREATE TABLE IF NOT EXISTS "ticket_event_due_date_changed" (
  "id"           UUID PRIMARY KEY REFERENCES "ticket_event",
  "src_due_date" TIMESTAMP,
  "dst_due_date" TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "ticket_event_comment_text_changed" (
  "id"         UUID PRIMARY KEY REFERENCES "ticket_event",
  "comment_id" UUID NOT NULL REFERENCES "comment",
  "src_text"   TEXT NOT NULL,
  "dst_text"   TEXT NOT NULL
);
CREATE INDEX ON "ticket_event_comment_text_changed" ("comment_id");

CREATE TABLE IF NOT EXISTS "ticket_event_tag_added" (
  "id"            UUID PRIMARY KEY REFERENCES "ticket_event",
  "ticket_tag_id" UUID NOT NULL REFERENCES "ticket_tag"
);
CREATE INDEX ON "ticket_event_tag_added" ("ticket_tag_id");

CREATE TABLE IF NOT EXISTS "ticket_event_tag_removed" (
  "id"            UUID PRIMARY KEY REFERENCES "ticket_event",
  "ticket_tag_id" UUID NOT NULL REFERENCES "ticket_tag"
);
CREATE INDEX ON "ticket_event_tag_removed" ("ticket_tag_id");

CREATE TABLE IF NOT EXISTS "ticket_event_user_added" (
  "id"                UUID PRIMARY KEY REFERENCES "ticket_event",
  "user_id"           UUID NOT NULL REFERENCES "user",
  "assignment_tag_id" UUID NOT NULL REFERENCES "assignment_tag"
);
CREATE INDEX ON "ticket_event_user_added" ("user_id");
CREATE INDEX ON "ticket_event_user_added" ("assignment_tag_id");

CREATE TABLE IF NOT EXISTS "ticket_event_user_removed" (
  "id"                UUID PRIMARY KEY REFERENCES "ticket_event",
  "user_id"           UUID NOT NULL REFERENCES "user",
  "assignment_tag_id" UUID NOT NULL REFERENCES "assignment_tag"
);
CREATE INDEX ON "ticket_event_user_removed" ("user_id");
CREATE INDEX ON "ticket_event_user_removed" ("assignment_tag_id");

CREATE TABLE IF NOT EXISTS "ticket_event_mention_added" (
  "id"         UUID PRIMARY KEY REFERENCES "ticket_event",
  "comment_id" UUID NOT NULL REFERENCES "comment",
  "ticket_id"  UUID NOT NULL REFERENCES "ticket"
);
CREATE INDEX ON "ticket_event_mention_added" ("comment_id");
CREATE INDEX ON "ticket_event_mention_added" ("ticket_id");

CREATE TABLE IF NOT EXISTS "ticket_event_mention_removed" (
  "id"         UUID PRIMARY KEY REFERENCES "ticket_event",
  "comment_id" UUID NOT NULL REFERENCES "comment",
  "ticket_id"  UUID NOT NULL REFERENCES "ticket"
);
CREATE INDEX ON "ticket_event_mention_removed" ("comment_id");
CREATE INDEX ON "ticket_event_mention_removed" ("ticket_id");

CREATE TABLE IF NOT EXISTS "ticket_event_logged_time_added" (
  "id"               UUID PRIMARY KEY REFERENCES "ticket_event",
  "comment_id"       UUID   NOT NULL REFERENCES "comment",
  "time_category_id" UUID   NOT NULL REFERENCES "time_category",
  "time"             BIGINT NOT NULL
);
CREATE INDEX ON "ticket_event_logged_time_added" ("comment_id");
CREATE INDEX ON "ticket_event_logged_time_added" ("time_category_id");

CREATE TABLE IF NOT EXISTS "ticket_event_logged_time_removed" (
  "id"               UUID PRIMARY KEY REFERENCES "ticket_event",
  "comment_id"       UUID   NOT NULL REFERENCES "comment",
  "time_category_id" UUID   NOT NULL REFERENCES "time_category",
  "time"             BIGINT NOT NULL
);
CREATE INDEX ON "ticket_event_logged_time_removed" ("comment_id");
CREATE INDEX ON "ticket_event_logged_time_removed" ("time_category_id");


CREATE TABLE IF NOT EXISTS "kanban_cell"(
  "id"             UUID PRIMARY KEY,
  "ticket_tag_id"  UUID NOT NULL REFERENCES "ticket_tag",
  "ticket_id"      UUID NOT NULL REFERENCES "ticket",
  "order"          INTEGER NOT NULL
);

CREATE VIEW view_progress AS
  SELECT
    t.*,
    CASE WHEN t.total_initial_estimated_time = 0
      THEN NULL
    ELSE t.total_logged_time :: REAL / t.total_initial_estimated_time :: REAL END AS total_initial_progress,
    CASE WHEN t.total_current_estimated_time = 0
      THEN NULL
    ELSE t.total_logged_time :: REAL / t.total_current_estimated_time :: REAL END AS total_progress
  FROM (
         SELECT
           t.id                                                         AS ticket_id,
           (SELECT coalesce(sum(tt.initial_estimated_time), 0)
            FROM ticket tt
            WHERE tt.id = t.id OR tt.parent_ticket_id = t.id) :: BIGINT AS total_initial_estimated_time,
           (SELECT coalesce(sum(tt.current_estimated_time), 0)
            FROM ticket tt
            WHERE tt.id = t.id OR tt.parent_ticket_id = t.id) :: BIGINT AS total_current_estimated_time,
           (SELECT coalesce(sum(lt.time), 0)
            FROM ticket tt
              JOIN comment cc ON cc.ticket_id = tt.id
              JOIN logged_time lt ON lt.comment_id = cc.id
            WHERE (tt.id = t.id OR tt.parent_ticket_id = t.id) and not lt.canceled) :: BIGINT AS total_logged_time,
           (SELECT coalesce(sum(lt.time), 0)
            FROM ticket tt
              JOIN comment cc ON cc.ticket_id = tt.id
              JOIN logged_time lt ON lt.comment_id = cc.id
            WHERE tt.id = t.id and not lt.canceled) :: BIGINT AS logged_time
         FROM ticket t
       ) t;
COMMIT;
