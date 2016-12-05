BEGIN;
DELETE FROM "ticket_event_comment_text_changed";
DELETE FROM "ticket_event_current_estimated_time_changed";
DELETE FROM "ticket_event_due_date_changed";
DELETE FROM "ticket_event_initial_estimated_time_changed";
DELETE FROM "ticket_event_logged_time_added";
DELETE FROM "ticket_event_logged_time_removed";
DELETE FROM "ticket_event_mention_added";
DELETE FROM "ticket_event_parent_changed";
DELETE FROM "ticket_event_state_changed";
DELETE FROM "ticket_event_story_points_changed";
DELETE FROM "ticket_event_tag_added";
DELETE FROM "ticket_event_tag_removed";
DELETE FROM "ticket_event_title_changed";
DELETE FROM "ticket_event_user_added";
DELETE FROM "ticket_event_user_removed";
DELETE FROM "ticket_event";

DELETE FROM "logged_time";
DELETE FROM "time_category";
DELETE FROM "assigned_ticket_user";
DELETE FROM "assignment_tag";
DELETE FROM "assigned_ticket_tag";
UPDATE TICKET
SET description_comment_id = NULL;
DELETE FROM "comment";
DELETE FROM "ticket";
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = NULL;
DELETE FROM "ticket_tag";
DELETE FROM "ticket_tag_group";
DELETE FROM "member";
DELETE FROM "project";
DELETE FROM "user";

--########################################## USERS ###################################################################
--####################################################################################################################
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000101', 'admit', 'admin@ticktag.a', 'Admiral Admin',
   '$2a$10$mTEkiQq2Wo./aqfekJHPk.5sG8JLWqWYbtMODwk9xQwQp0GtkCiM.', 'ADMIN', '00000000-0001-0000-0000-abcdef123641',
   NULL); --aaaa
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000102', 'obelix', 'observer@ticktag.a', 'Obelix Observer',
   '$2a$10$Ydzo0FR5x8ZweeaeIQS2gevmLqsZuS37.bWRYy.f.u62NG00MAOcS', 'OBSERVER', '00000000-0001-0000-2343-abcdef123641',
   NULL); --bbbb
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000103', 'userla', 'user1@ticktag.a', 'Ursula User',
   '$2a$10$OgvbSbiDxizgC/6K3dhVwO8iY6.QFS6f2PvE1AyJS1Vmo6Rnb3Gve', 'USER', '00000000-0001-8676-0000-abcdef123641',
   NULL); --cccc
--######################################## PROJECT ###################################################################
--####################################################################################################################
INSERT INTO "project" VALUES
  ('00000000-0002-0000-0000-000000000101', 'Project One', 'Incredible Stuff ', '2016-07-03 08:49:05', NULL),
  ('00000000-0002-0000-0000-000000000102', 'Project Two', 'Amazing Too', '2016-08-26 21:57:39', NULL),
  ('00000000-0002-0000-0000-000000000103', 'Project Three', 'Quite Astonishing', '2016-01-17 16:00:33', NULL),
  ('00000000-0002-0000-0000-000000000104', 'Project Four', 'Pretty Boring', '2016-01-17 16:00:33', NULL);
--######################################## MEMBERS ###################################################################
--####################################################################################################################
INSERT INTO "member" VALUES
  ('00000000-0001-0000-0000-000000000101', '00000000-0002-0000-0000-000000000101', 'ADMIN',
   to_date('2016-11-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000101', '00000000-0002-0000-0000-000000000102', 'USER',
   to_date('2016-12-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000101', '00000000-0002-0000-0000-000000000103', 'OBSERVER',
   to_date('2016-12-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000102', '00000000-0002-0000-0000-000000000101', 'USER',
   to_date('2016-12-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000102', '00000000-0002-0000-0000-000000000102', 'OBSERVER',
   to_date('2016-10-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000102', '00000000-0002-0000-0000-000000000103', 'ADMIN',
   to_date('2016-10-13', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000103', '00000000-0002-0000-0000-000000000101', 'OBSERVER',
   to_date('2016-12-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000103', '00000000-0002-0000-0000-000000000102', 'ADMIN',
   to_date('2016-10-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000103', '00000000-0002-0000-0000-000000000103', 'USER',
   to_date('2016-10-13', 'YYYY-MM-DD'));
--######################################## TICKET ###################################################################
--####################################################################################################################

INSERT INTO PUBLIC.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, OPEN, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000101', 1, NULL, '00000000-0002-0000-0000-000000000101',
                                                '00000000-0001-0000-0000-000000000101', NULL,
                                                '2016-11-16 17:06:07.221000', 'Project 1 Ticket One', TRUE, 10, 20, 25,
        '2016-11-20 17:07:05.554000');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0004-0000-0000-000000000101', '00000000-0001-0000-0000-000000000101',
        '00000000-0003-0000-0000-000000000101', '2016-11-16 17:09:59.019000', 'Description Project1 Ticket1');
UPDATE PUBLIC.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000101'
WHERE id = '00000000-0003-0000-0000-000000000101';
-- ######

INSERT INTO PUBLIC.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, OPEN, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000102', 1, NULL, '00000000-0002-0000-0000-000000000102',
                                                '00000000-0001-0000-0000-000000000101', NULL,
                                                '2016-11-16 17:06:07.221000', 'Project 2 Ticket One', TRUE, 10, 20, 25,
        '2016-11-20 17:07:05.554000');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0004-0000-0000-000000000102', '00000000-0001-0000-0000-000000000101',
        '00000000-0003-0000-0000-000000000102', '2016-11-16 17:09:59.019000', 'Description Project2 Ticket1');
UPDATE PUBLIC.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000102'
WHERE id = '00000000-0003-0000-0000-000000000102';
-- ######

INSERT INTO PUBLIC.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, OPEN, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000103', 1, NULL, '00000000-0002-0000-0000-000000000103',
                                                '00000000-0001-0000-0000-000000000101', NULL,
                                                '2016-11-16 17:06:07.221000', 'Project 3 Ticket One', TRUE, 10, 20, 25,
        '2016-11-20 17:07:05.554000');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0004-0000-0000-000000000103', '00000000-0001-0000-0000-000000000101',
        '00000000-0003-0000-0000-000000000103', '2016-11-16 17:09:59.019000', 'Description Project3 Ticket1');
UPDATE PUBLIC.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000103'
WHERE id = '00000000-0003-0000-0000-000000000103';
-- ######

INSERT INTO PUBLIC.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, OPEN, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000104', 1, NULL, '00000000-0002-0000-0000-000000000104',
                                                '00000000-0001-0000-0000-000000000101', NULL,
                                                '2016-11-16 17:06:07.221000', 'Project 4 Ticket One', TRUE, 10, 20, 25,
        '2016-11-20 17:07:05.554000');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0004-0000-0000-000000000104', '00000000-0001-0000-0000-000000000101',
        '00000000-0003-0000-0000-000000000104', '2016-11-16 17:09:59.019000', 'Description Project4 Ticket1');
UPDATE PUBLIC.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000104'
WHERE id = '00000000-0003-0000-0000-000000000104';
-- ######

INSERT INTO PUBLIC.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, OPEN, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000105', 2, NULL, '00000000-0002-0000-0000-000000000101',
                                                '00000000-0001-0000-0000-000000000101', NULL,
                                                '2016-11-16 17:06:07.221000', 'Project 1 Ticket Two', TRUE, 10, 20, 25,
        '2016-11-20 17:07:05.554000');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0004-0000-0000-000000000105', '00000000-0001-0000-0000-000000000101',
        '00000000-0003-0000-0000-000000000104', '2016-11-16 17:09:59.019000', 'Description Project4 Ticket1');
UPDATE PUBLIC.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000105'
WHERE id = '00000000-0003-0000-0000-000000000104';

--######################################## COMMENTS ##################################################################
--####################################################################################################################

-- Ticket 00000000-0003-0000-0000-000000000101
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0008-0000-0101-000000000001', '00000000-0001-0000-0000-000000000101',
        '00000000-0003-0000-0000-000000000101', '2016-11-16 17:09:59.019000', 'P1 T1 Comment 1');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0008-0000-0101-000000000002', '00000000-0001-0000-0000-000000000102',
        '00000000-0003-0000-0000-000000000101', '2016-11-17 07:00:00.058987', 'P1 T1 Comment 2');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0008-0000-0101-000000000003', '00000000-0001-0000-0000-000000000103',
        '00000000-0003-0000-0000-000000000101', '2016-11-17 10:15:58.055050', 'P1 T1 Comment 3');
-- ######

-- Ticket 00000000-0003-0000-0000-000000000102
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0008-0000-0102-000000000001', '00000000-0001-0000-0000-000000000101',
        '00000000-0003-0000-0000-000000000102', '2016-11-16 17:09:59.019000', 'P2 T1 Comment 1');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0008-0000-0102-000000000002', '00000000-0001-0000-0000-000000000102',
        '00000000-0003-0000-0000-000000000102', '2016-11-17 07:00:00.058987', 'P2 T1 Comment 2');
INSERT INTO PUBLIC.comment (id, user_id, ticket_id, create_time, TEXT)
VALUES ('00000000-0008-0000-0102-000000000003', '00000000-0001-0000-0000-000000000103',
        '00000000-0003-0000-0000-000000000102', '2016-11-17 10:15:58.055050', 'P2 T1 Comment 3');
-- ######

--######################################## ASSIGNMENT TAG ############################################################
--####################################################################################################################

INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color) VALUES
  ('00000000-0006-0000-0000-000000000101', '00000000-0002-0000-0000-000000000101', 'dev', 'dev', 'ff0000'),
  ('00000000-0006-0000-0000-000000000102', '00000000-0002-0000-0000-000000000101', 'test', 'test', '0000ff'),
  ('00000000-0006-0000-0000-000000000103', '00000000-0002-0000-0000-000000000101', 'rev', 'rev', 'ff000f');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color) VALUES
  ('00000000-0006-0000-0000-000000000104', '00000000-0002-0000-0000-000000000102', 'dev', 'dev', 'ff0000'),
  ('00000000-0006-0000-0000-000000000105', '00000000-0002-0000-0000-000000000102', 'test', 'test', '0000ff'),
  ('00000000-0006-0000-0000-000000000106', '00000000-0002-0000-0000-000000000102', 'rev', 'rev', 'ff000f');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color) VALUES
  ('00000000-0006-0000-0000-000000000107', '00000000-0002-0000-0000-000000000103', 'dev', 'dev', 'ff0000'),
  ('00000000-0006-0000-0000-000000000108', '00000000-0002-0000-0000-000000000103', 'test', 'test', '0000ff'),
  ('00000000-0006-0000-0000-000000000109', '00000000-0002-0000-0000-000000000103', 'rev', 'rev', 'ff000f');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color) VALUES
  ('00000000-0006-0000-0000-000000000110', '00000000-0002-0000-0000-000000000104', 'dev', 'dev', 'ff0000'),
  ('00000000-0006-0000-0000-000000000111', '00000000-0002-0000-0000-000000000104', 'test', 'test', '0000ff'),
  ('00000000-0006-0000-0000-000000000112', '00000000-0002-0000-0000-000000000104', 'rev', 'rev', 'ff000f');
--######################################## ASSIGNED TICKET USER#######################################################
--####################################################################################################################

INSERT INTO PUBLIC.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000101', '00000000-0006-0000-0000-000000000101',
        '00000000-0001-0000-0000-000000000101');
INSERT INTO PUBLIC.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000101', '00000000-0006-0000-0000-000000000102',
        '00000000-0001-0000-0000-000000000103');
INSERT INTO PUBLIC.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000102', '00000000-0006-0000-0000-000000000104',
        '00000000-0001-0000-0000-000000000101');
INSERT INTO PUBLIC.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000102', '00000000-0006-0000-0000-000000000106',
        '00000000-0001-0000-0000-000000000103');

--Ticket 00000000-0003-0000-0000-00000000010 3&4 reserved for insert tests


--######################################## TIMECATEGORY ##############################################################
--####################################################################################################################

INSERT INTO PUBLIC.time_category (id, project_id, NAME, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000101', '00000000-0002-0000-0000-000000000101', 'dev', 'dev');
INSERT INTO PUBLIC.time_category (id, project_id, NAME, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000102', '00000000-0002-0000-0000-000000000101', 'plan', 'plan');
INSERT INTO PUBLIC.time_category (id, project_id, NAME, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000103', '00000000-0002-0000-0000-000000000102', 'dev', 'dev');
INSERT INTO PUBLIC.time_category (id, project_id, NAME, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000104', '00000000-0002-0000-0000-000000000102', 'plan', 'plan');
INSERT INTO PUBLIC.time_category (id, project_id, NAME, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000105', '00000000-0002-0000-0000-000000000103', 'dev', 'dev');
INSERT INTO PUBLIC.time_category (id, project_id, NAME, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000106', '00000000-0002-0000-0000-000000000103', 'plan', 'plan');
INSERT INTO PUBLIC.time_category (id, project_id, NAME, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000107', '00000000-0002-0000-0000-000000000104', 'dev', 'dev');
INSERT INTO PUBLIC.time_category (id, project_id, NAME, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000108', '00000000-0002-0000-0000-000000000104', 'plan', 'plan');

--######################################## TICKET TAG GROUP ##########################################################
--####################################################################################################################
INSERT INTO public.ticket_tag_group (id, project_id, default_ticket_tag_id, name, exclusive) VALUES
  ('00000000-0009-0000-0000-000000000101', '00000000-0002-0000-0000-000000000101', NULL, 'Type', TRUE),
  ('00000000-0009-0000-0000-000000000102', '00000000-0002-0000-0000-000000000101', NULL, 'Priority', TRUE),
  ('00000000-0009-0000-0000-000000000103', '00000000-0002-0000-0000-000000000101', NULL, 'Random', FALSE);

INSERT INTO public.ticket_tag_group (id, project_id, default_ticket_tag_id, name, exclusive) VALUES
  ('00000000-0009-0000-0000-000000000104', '00000000-0002-0000-0000-000000000102', NULL, 'Type', TRUE),
  ('00000000-0009-0000-0000-000000000105', '00000000-0002-0000-0000-000000000102', NULL, 'Priority', TRUE),
  ('00000000-0009-0000-0000-000000000106', '00000000-0002-0000-0000-000000000102', NULL, 'Random', FALSE);

INSERT INTO public.ticket_tag_group (id, project_id, default_ticket_tag_id, name, exclusive) VALUES
  ('00000000-0009-0000-0000-000000000107', '00000000-0002-0000-0000-000000000103', NULL, 'Type', TRUE),
  ('00000000-0009-0000-0000-000000000108', '00000000-0002-0000-0000-000000000103', NULL, 'Priority', TRUE),
  ('00000000-0009-0000-0000-000000000109', '00000000-0002-0000-0000-000000000103', NULL, 'Random', FALSE);

--######################################## TICKET TAG ################################################################
--####################################################################################################################
INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order") VALUES
  ('00000000-0005-0000-0000-000000000101', '00000000-0009-0000-0000-000000000101', 'Feature', 'feature', '008000', 1),
  ('00000000-0005-0000-0000-000000000102', '00000000-0009-0000-0000-000000000101', 'Bug', 'bug', '006000', 2),
  ('00000000-0005-0000-0000-000000000103', '00000000-0009-0000-0000-000000000101', 'Request', 'request', '007000', 3),
  ('00000000-0005-0000-0000-000000000104', '00000000-0009-0000-0000-000000000102', 'Urgent', 'urgent', '008000', 1),
  ('00000000-0005-0000-0000-000000000105', '00000000-0009-0000-0000-000000000102', 'High', 'high', '006000', 2),
  ('00000000-0005-0000-0000-000000000106', '00000000-0009-0000-0000-000000000102', 'Low', 'low', '007000', 3),
  ('00000000-0005-0000-0000-000000000107', '00000000-0009-0000-0000-000000000103', 'Boring', 'urgent', '008000', 1),
  ('00000000-0005-0000-0000-000000000108', '00000000-0009-0000-0000-000000000103', 'Amazing', 'high', '006000', 2),
  ('00000000-0005-0000-0000-000000000109', '00000000-0009-0000-0000-000000000103', 'Wow', 'low', '007000', 3);
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000101'
WHERE id = '00000000-0009-0000-0000-000000000101';
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000104'
WHERE id = '00000000-0009-0000-0000-000000000102';
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000107'
WHERE id = '00000000-0009-0000-0000-000000000103';

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order") VALUES
  ('00000000-0005-0000-0000-000000000110', '00000000-0009-0000-0000-000000000104', 'Feature', 'feature', '008000', 1),
  ('00000000-0005-0000-0000-000000000111', '00000000-0009-0000-0000-000000000104', 'Bug', 'bug', '006000', 2),
  ('00000000-0005-0000-0000-000000000112', '00000000-0009-0000-0000-000000000104', 'Request', 'request', '007000', 3),
  ('00000000-0005-0000-0000-000000000113', '00000000-0009-0000-0000-000000000105', 'Urgent', 'urgent', '008000', 1),
  ('00000000-0005-0000-0000-000000000114', '00000000-0009-0000-0000-000000000105', 'High', 'high', '006000', 2),
  ('00000000-0005-0000-0000-000000000115', '00000000-0009-0000-0000-000000000105', 'Low', 'low', '007000', 3),
  ('00000000-0005-0000-0000-000000000116', '00000000-0009-0000-0000-000000000106', 'Boring', 'urgent', '008000', 1),
  ('00000000-0005-0000-0000-000000000117', '00000000-0009-0000-0000-000000000106', 'Amazing', 'high', '006000', 2),
  ('00000000-0005-0000-0000-000000000118', '00000000-0009-0000-0000-000000000106', 'Wow', 'low', '007000', 3);
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000110'
WHERE id = '00000000-0009-0000-0000-000000000104';
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000113'
WHERE id = '00000000-0009-0000-0000-000000000105';
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000116'
WHERE id = '00000000-0009-0000-0000-000000000106';

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order") VALUES
  ('00000000-0005-0000-0000-000000000119', '00000000-0009-0000-0000-000000000107', 'Feature', 'feature', '008000', 1),
  ('00000000-0005-0000-0000-000000000120', '00000000-0009-0000-0000-000000000107', 'Bug', 'bug', '006000', 2),
  ('00000000-0005-0000-0000-000000000121', '00000000-0009-0000-0000-000000000107', 'Request', 'request', '007000', 3),
  ('00000000-0005-0000-0000-000000000122', '00000000-0009-0000-0000-000000000108', 'Urgent', 'urgent', '008000', 1),
  ('00000000-0005-0000-0000-000000000123', '00000000-0009-0000-0000-000000000108', 'High', 'high', '006000', 2),
  ('00000000-0005-0000-0000-000000000124', '00000000-0009-0000-0000-000000000108', 'Low', 'low', '007000', 3),
  ('00000000-0005-0000-0000-000000000125', '00000000-0009-0000-0000-000000000109', 'Boring', 'urgent', '008000', 1),
  ('00000000-0005-0000-0000-000000000126', '00000000-0009-0000-0000-000000000109', 'Amazing', 'high', '006000', 2),
  ('00000000-0005-0000-0000-000000000127', '00000000-0009-0000-0000-000000000109', 'Wow', 'low', '007000', 3);
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000119'
WHERE id = '00000000-0009-0000-0000-000000000107';
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000122'
WHERE id = '00000000-0009-0000-0000-000000000108';
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000125'
WHERE id = '00000000-0009-0000-0000-000000000109';

--######################################## TICKET TAG RELATION########################################################
--####################################################################################################################

INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id) VALUES
  ('00000000-0003-0000-0000-000000000101', '00000000-0005-0000-0000-000000000101'),
  ('00000000-0003-0000-0000-000000000101', '00000000-0005-0000-0000-000000000104'),
  ('00000000-0003-0000-0000-000000000101', '00000000-0005-0000-0000-000000000107'),
  ('00000000-0003-0000-0000-000000000101', '00000000-0005-0000-0000-000000000108');

-- Tags for Tickets 00000000-0003-0000-0000-0000000001 2&3 reserved for insert tests

COMMIT;