BEGIN;

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
COMMIT;


BEGIN;
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000001', 'user_a', 'a@a.a', 'Mr. A',
   '$2a$10$mTEkiQq2Wo./aqfekJHPk.5sG8JLWqWYbtMODwk9xQwQp0GtkCiM.', 'ADMIN', '00000000-0001-0000-0000-abcdef123641',
   NULL); --aaaa
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000002', 'user_b', 'b@b.b', 'Berta Berta',
   '$2a$10$Ydzo0FR5x8ZweeaeIQS2gevmLqsZuS37.bWRYy.f.u62NG00MAOcS', 'USER', '00000000-0001-0000-2343-abcdef123641',
   NULL); --bbbb
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000003', 'user_c', 'c@c.c', 'Gaius Iulius Caesar',
   '$2a$10$OgvbSbiDxizgC/6K3dhVwO8iY6.QFS6f2PvE1AyJS1Vmo6Rnb3Gve', 'OBSERVER', '00000000-0001-8676-0000-abcdef123641',
   NULL); --cccc
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0000-0000-0000-000000000000', 'admin', 'admin@admin.invalid', 'Admin',
   '$2a$10$dXjkyD704.vNyYWrsmEbrewcMeWIz1fDcjVVuggUyLmExGQQD3RGC', 'ADMIN', '9a030c2e-b2c7-4d98-825b-92c148897f4a',
   NULL);
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('660f2968-aa46-4870-bcc5-a3805366cff2', 'drasko', 'stefan.draskovits@test.at', 'Stefan Draskovits',
   '$2a$10$NuX1RqGiFg38qjF75b88J.oWw271xVYhsPvLRxHAQHnS2V9i0nNza', 'ADMIN', '4aa33174-bdf2-4d33-b80f-d7fb8d121923',
   E'\\x'); --stefan-supersecure
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('93ef43d9-20b7-461a-b960-2d1e89ba099f', 'heinzl', 'michael.heinzl@test.de', 'Michael Heinzl',
   '$2a$10$.dLg4Vgt7JrP.564p/tPQOm.TLoy3HieFP1ZpnyWVPkJDYrG6r.Ce', 'OBSERVER', '370f4e86-1ebf-4b70-a113-add96d0905e1',
   E'\\x'); --michael-supersecure


-- BEGIN all role/project role member permutations DO NOT ALTER
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000101', 'admit', 'admin@ticktag.a', 'Admiral Admin',
   '$2a$10$mTEkiQq2Wo./aqfekJHPk.5sG8JLWqWYbtMODwk9xQwQp0GtkCiM.', 'ADMIN', '00000000-0001-0000-0000-abcdef123641',
   NULL); --aaaa
INSERT INTO public."user" (id, username, name, mail, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000102', 'obelix', 'observer@ticktag.a', 'Obelix Observer',
   '$2a$10$Ydzo0FR5x8ZweeaeIQS2gevmLqsZuS37.bWRYy.f.u62NG00MAOcS', 'OBSERVER', '00000000-0001-0000-2343-abcdef123641',
   NULL); --bbbb
INSERT INTO public."user" (id, username, name, mail, password_hash, role, current_token, profile_pic) VALUES
  ('00000000-0001-0000-0000-000000000103', 'userla', 'user1@ticktag.a', 'Ursula User',
   '$2a$10$OgvbSbiDxizgC/6K3dhVwO8iY6.QFS6f2PvE1AyJS1Vmo6Rnb3Gve', 'USER', '00000000-0001-8676-0000-abcdef123641',
   NULL); --cccc
-- END all role/project role member permutations DO NOT ALTER


INSERT INTO "project" VALUES
  ('00000000-0002-0000-0000-000000000001', 'Bitchip', 'Pfizer Consumer Healthcare', '2016-07-03 08:49:05', NULL),
  ('00000000-0002-0000-0000-000000000002', 'Veribet', 'H E B', '2016-08-26 21:57:39', NULL),
  ('00000000-0002-0000-0000-000000000003', 'Alpha', 'Major Pharmaceuticals', '2016-01-17 16:00:33', NULL),
  ('00000000-0002-0000-0000-000000000004', 'Home Ing', 'Publix Super Markets Inc', '2016-03-13 04:21:56', NULL),
  ('00000000-0002-0000-0000-000000000005', 'Holdlamis', 'Cal Pharma', '2016-01-02 01:03:12', NULL),
  ('00000000-0002-0000-0000-000000000006', 'Duobam', 'Proficient Rx LP', '2016-09-29 16:27:21', NULL),
  ('00000000-0002-0000-0000-000000000007', 'Keylex', 'CHANEL PARFUMS BEAUTE', '2015-02-13 09:07:10', NULL),
  ('00000000-0002-0000-0000-000000000008', 'Prodder', 'Nova Homeopathic Therapeutics, Inc.', '2015-11-25 02:56:38', NULL),
  ('00000000-0002-0000-0000-000000000009', 'Holdlamis', 'McKesson Corporation', '2016-03-27 12:41:13', NULL),
  ('00000000-0002-0000-0000-000000000010', 'Asoka', 'Beutlich Pharmaceuticals LLC', '2015-12-11 03:32:52', NULL),
  ('00000000-0002-0000-0000-000000000011', 'Y-Solowarm', 'Apotheca Company', '2015-01-29 21:31:50', NULL),
  ('00000000-0002-0000-0000-000000000012', 'Flowdesk', 'REMEDYREPACK INC.', '2015-04-06 14:47:07', NULL),
  ('00000000-0002-0000-0000-000000000013', 'Asoka', 'Ecolab Inc.', '2016-03-01 03:51:11', NULL),
  ('00000000-0002-0000-0000-000000000014', 'Domainer', 'Barr Laboratories Inc.', '2016-08-01 04:11:30', NULL),
  ('00000000-0002-0000-0000-000000000015', 'Bamity', 'CVS Pharmacy', '2016-03-24 17:48:19', NULL),
  ('00000000-0002-0000-0000-000000000016', 'Otcom', 'Legacy Pharmaceutical Packaging', '2014-11-24 13:49:23', NULL),
  ('00000000-0002-0000-0000-000000000017', 'Voltsillam', 'STAT Rx USA LLC', '2015-09-27 20:29:49', NULL),
  ('00000000-0002-0000-0000-000000000018', 'Overhold', 'Physicians Total Care, Inc.', '2016-01-01 00:10:40', NULL),
  ('00000000-0002-0000-0000-000000000019', 'It', 'Procter & Gamble Manufacturing Company', '2016-04-22 18:36:36', NULL),
  ('00000000-0002-0000-0000-000000000020', 'Sonsing', 'Bare Escentuals Beauty Inc.', '2016-07-02 06:35:50', NULL),
  ('00000000-0002-0000-0000-000000000021', 'Lotstring', 'Baxter Healthcare Corporation', '2015-06-07 02:07:32', NULL),
  ('00000000-0002-0000-0000-000000000022', 'Sonsing', 'ALK-Abello, Inc.', '2015-07-23 03:06:10', NULL),
  ('00000000-0002-0000-0000-000000000023', 'Biodex', 'Cardinal Health', '2015-10-16 08:39:17', NULL),
  ('00000000-0002-0000-0000-000000000024', 'Quo Lux', 'Natural Health Supply', '2016-11-05 14:21:54', NULL),
  ('00000000-0002-0000-0000-000000000025', 'Alpha', 'Nelco Laboratories, Inc.', '2016-01-10 19:14:31', NULL),
  ('00000000-0002-0000-0000-000000000026', 'Alphazap', 'Bristol-Myers Squibb de Mexico, S. de R.L. de C.V.', '2015-08-19 08:26:39', NULL),
  ('00000000-0002-0000-0000-000000000027', 'Aerified', 'Migranade Inc.', '2015-11-09 04:50:37', NULL),
  ('00000000-0002-0000-0000-000000000028', 'Holdlamis', 'Ventura Corporation, LTD', '2015-08-25 19:25:52', NULL),
  ('00000000-0002-0000-0000-000000000029', 'Toughjoyfax', 'Lake Erie Medical & Surgical Supply DBA Quality Care Products LLC', '2016-03-22 03:15:08', NULL),
  ('00000000-0002-0000-0000-000000000030', 'Stim', 'REMEDYREPACK INC.', '2014-12-20 11:11:48', NULL),
  ('00000000-0002-0000-0000-000000000031', 'Matsoft', 'Energizer Personal Care LLC', '2015-11-24 20:48:37', NULL),
  ('00000000-0002-0000-0000-000000000032', 'Alphazap', 'Sun Pharmaceutical Industries Limited', '2015-01-22 12:35:45', NULL),
  ('00000000-0002-0000-0000-000000000033', 'Holdlamis', 'Prasco Laboratories', '2015-10-21 11:47:18', NULL),
  ('00000000-0002-0000-0000-000000000034', 'Hatity', 'Kareway Product, Inc.', '2016-08-07 05:56:37', NULL),
  ('00000000-0002-0000-0000-000000000035', 'Stringtough', 'West-ward Pharmaceutical Corp', '2016-09-04 11:47:27', NULL),
  ('00000000-0002-0000-0000-000000000036', 'Toughjoyfax', 'ALK-Abello, Inc.', '2015-03-21 16:50:20', NULL),
  ('00000000-0002-0000-0000-000000000037', 'Daltfresh', 'Delon Laboratories (1990) Ltd', '2015-08-28 05:23:49', NULL),
  ('00000000-0002-0000-0000-000000000038', 'Bigtax', 'Cardinal Health', '2016-09-26 17:56:34', NULL),
  ('00000000-0002-0000-0000-000000000039', 'Lotstring', 'PD-Rx Pharmaceuticals, Inc.', '2016-05-18 13:08:47', NULL),
  ('00000000-0002-0000-0000-000000000040', 'Latlux', 'Walgreen Company', '2016-09-10 11:20:08', NULL),
  ('00000000-0002-0000-0000-000000000041', 'Kanlam', 'Kmart Corporation', '2016-11-10 10:23:48', NULL),
  ('00000000-0002-0000-0000-000000000042', 'Asoka', 'Sandoz Inc', '2015-10-02 14:20:11', NULL),
  ('00000000-0002-0000-0000-000000000043', 'Bytecard', 'Kinray', '2015-07-22 04:55:46', NULL),
  ('00000000-0002-0000-0000-000000000044', 'Ronstring', 'A-S Medication Solutions LLC', '2015-07-17 08:58:21', NULL),
  ('00000000-0002-0000-0000-000000000045', 'Sonsing', 'Lancaster S.A.M.', '2016-02-02 22:33:57', NULL),
  ('00000000-0002-0000-0000-000000000046', 'Flexidy', 'Natures Way Holding Co', '2015-10-14 03:40:42', NULL),
  ('00000000-0002-0000-0000-000000000047', 'Bigtax', 'American Sales Company', '2015-03-17 00:05:49', NULL),
  ('00000000-0002-0000-0000-000000000048', 'Sonair', 'WAL-MART STORES INC', '2016-01-07 18:13:31', NULL),
  ('00000000-0002-0000-0000-000000000049', 'Home Ing', 'Aurolife Pharma, LLC', '2016-03-27 16:25:32', NULL),
  ('00000000-0002-0000-0000-000000000050', 'Konklux', 'ALK-Abello, Inc.', '2016-07-17 03:47:25', NULL),
  -- BEGIN all role/project role member permutations DO NOT ALTER
  ('00000000-0002-0000-0000-000000000101', 'Project One', 'Incredible Stuff ', '2016-07-03 08:49:05', NULL),
  ('00000000-0002-0000-0000-000000000102', 'Project Two', 'Amazing Too', '2016-08-26 21:57:39', NULL),
  ('00000000-0002-0000-0000-000000000103', 'Project Three', 'Quite Astonishing', '2016-01-17 16:00:33', NULL),
  ('00000000-0002-0000-0000-000000000104', 'Project Four', 'Pretty Boring', '2016-01-17 16:00:33', NULL);
-- END all role/project role member permutations DO NOT ALTER
INSERT INTO "member" VALUES
  ('00000000-0001-0000-0000-000000000001', '00000000-0002-0000-0000-000000000001', 'ADMIN',
   to_date('2016-11-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000002', '00000000-0002-0000-0000-000000000001', 'ADMIN',
   to_date('2016-12-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000002', '00000000-0002-0000-0000-000000000002', 'ADMIN',
   to_date('2016-10-11', 'YYYY-MM-DD')),
  ('00000000-0001-0000-0000-000000000003', '00000000-0002-0000-0000-000000000002', 'ADMIN',
   to_date('2016-10-13', 'YYYY-MM-DD')),
  ('93ef43d9-20b7-461a-b960-2d1e89ba099f', '00000000-0002-0000-0000-000000000001', 'ADMIN',
   to_date('2016-11-11', 'YYYY-MM-DD')),

  -- BEGIN all role/project role member permutations DO NOT ALTER
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
-- END all role/project role permutations DO NOT ALTER

COMMIT;

--TICKETS


INSERT INTO public.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, open, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000001', 1, NULL, '00000000-0002-0000-0000-000000000001',
                                                '660f2968-aa46-4870-bcc5-a3805366cff2',
                                                NULL,
                                                '2016-11-16 17:06:07.221000',
                                                'Added Models to Layout', TRUE, 10, 20, 25,
        '2016-11-20 17:07:05.554000');
INSERT INTO public.comment (id, user_id, ticket_id, create_time, text)
VALUES ('00000000-0004-0000-0000-000000000001', '660f2968-aa46-4870-bcc5-a3805366cff2',
        '00000000-0003-0000-0000-000000000001', '2016-11-16 17:09:59.019000', 'Hello World');

UPDATE public.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000001'
WHERE id = '00000000-0003-0000-0000-000000000001';


INSERT INTO public.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, open, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000002', 2, NULL, '00000000-0002-0000-0000-000000000001',
                                                '660f2968-aa46-4870-bcc5-a3805366cff2',
                                                NULL, '2016-11-16 18:06:07.221000',
                                                'Create Users View', TRUE, 10, 20, 25, '2016-11-20 17:07:05.554000');
INSERT INTO public.comment (id, user_id, ticket_id, create_time, text)
VALUES ('00000000-0004-0000-0000-000000000002', '660f2968-aa46-4870-bcc5-a3805366cff2',
        '00000000-0003-0000-0000-000000000002', '2016-11-16 17:09:59.019000', 'You have to do 3 sub Tasks');
UPDATE public.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000002'
WHERE id = '00000000-0003-0000-0000-000000000002';


INSERT INTO public.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, open, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000003', 3, '00000000-0003-0000-0000-000000000002',
                                                '00000000-0002-0000-0000-000000000001',
                                                '660f2968-aa46-4870-bcc5-a3805366cff2',
                                                NULL, '2016-11-16 18:06:07.221000',
                                                'UI Users View', FALSE, 10, 10, 10, '2016-11-20 17:07:05.554000');
INSERT INTO public.comment (id, user_id, ticket_id, create_time, text) VALUES
  ('00000000-0004-0000-0000-000000000003', '660f2968-aa46-4870-bcc5-a3805366cff2',
   '00000000-0003-0000-0000-000000000003', '2016-11-16 17:09:59.019000',
   'Design UI --Comment there is no Closed Event atm');
UPDATE public.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000003'
WHERE id = '00000000-0003-0000-0000-000000000003';


INSERT INTO public.comment (id, user_id, ticket_id, create_time, text) VALUES
  ('00000000-0004-0000-0000-000000000006', '660f2968-aa46-4870-bcc5-a3805366cff2',
   '00000000-0003-0000-0000-000000000003', '2016-11-16 18:09:59.019000', 'Finished');

INSERT INTO public.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, open, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000004', 4, '00000000-0003-0000-0000-000000000002',
                                                '00000000-0002-0000-0000-000000000001',
                                                '660f2968-aa46-4870-bcc5-a3805366cff2',
                                                NULL, '2016-11-16 18:06:07.221000',
                                                'Implement Users View', TRUE, 4, 25, 25, '2016-11-20 17:07:05.554000');
INSERT INTO public.comment (id, user_id, ticket_id, create_time, text) VALUES
  ('00000000-0004-0000-0000-000000000004', '660f2968-aa46-4870-bcc5-a3805366cff2',
   '00000000-0003-0000-0000-000000000004', '2016-11-16 17:09:59.019000', 'Implement Users View');
UPDATE public.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000004'
WHERE id = '00000000-0003-0000-0000-000000000004';


INSERT INTO public.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, open, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000005', 5, '00000000-0003-0000-0000-000000000002',
                                                '00000000-0002-0000-0000-000000000001',
                                                '660f2968-aa46-4870-bcc5-a3805366cff2',
                                                NULL, '2016-11-16 18:06:07.221000',
                                                'Test Users View', FALSE, 20, 20, 25, '2016-11-20 17:07:05.554000');
INSERT INTO public.comment (id, user_id, ticket_id, create_time, text) VALUES
  ('00000000-0004-0000-0000-000000000005', '660f2968-aa46-4870-bcc5-a3805366cff2',
   '00000000-0003-0000-0000-000000000005', '2016-11-16 17:09:59.019000', 'Test Users View');
UPDATE public.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000005'
WHERE id = '00000000-0003-0000-0000-000000000005';


INSERT INTO public.ticket (id, number, parent_ticket_id, project_id, created_by, description_comment_id, create_time, title, open, story_points, initial_estimated_time, current_estimated_time, due_date)
VALUES ('00000000-0003-0000-0000-000000000006', 6, NULL, '00000000-0002-0000-0000-000000000001',
                                                '93ef43d9-20b7-461a-b960-2d1e89ba099f',
                                                NULL, '2016-11-16 18:06:07.221000',
                                                'Set UP CI', FALSE, 20, 3.6e+13, 5.4e+13, '2016-11-20 17:07:05.554000');
INSERT INTO public.comment (id, user_id, ticket_id, create_time, text) VALUES
  ('00000000-0004-0000-0000-000000000007', '93ef43d9-20b7-461a-b960-2d1e89ba099f',
   '00000000-0003-0000-0000-000000000006', '2016-11-16 17:09:59.019', '# An exhibit of Markdown

This note demonstrates some of what [Markdown][1] is capable of doing.

*Note: Feel free to play with this page. Unlike regular notes, this doesn''t automatically save itself.*

## Basic formatting

Paragraphs can be written like so. A paragraph is the basic block of Markdown. A paragraph is what text will turn into when there is no reason it should become anything else.

Paragraphs must be separated by a blank line. Basic formatting of *italics* and **bold** is supported. This *can be **nested** like* so.

## Lists

### Ordered list

1. Item 1
2. A second item
3. Number 3
4. Ⅳ

*Note: the fourth item uses the Unicode character for [Roman numeral four][2].*

### Unordered list

* An item
* Another item
* Yet another item
* And there''s more...

## Paragraph modifiers

### Code block

    Code blocks are very useful for developers and other people who look at code or other things that are written in plain text. As you can see, it uses a fixed-width font.

You can also make `inline code` to add code into other things.

### Quote

> Here is a quote. What this is should be self explanatory. Quotes are automatically indented when they are used.

## Headings

There are six levels of headings. They correspond with the six levels of HTML headings. You''ve probably noticed them already in the page. Each level down uses one more hash character.

### Headings *can* also contain **formatting**

### They can even contain `inline code`

Of course, demonstrating what headings look like messes up the structure of the page.

I don''t recommend using more than three or four levels of headings here, because, when you''re smallest heading isn''t too small, and you''re largest heading isn''t too big, and you want each size up to look noticeably larger and more important, there there are only so many sizes that you can use.

## URLs

URLs can be made in a handful of ways:

* A named link to [MarkItDown][3]. The easiest way to do these is to select what you want to make a link and hit `Ctrl+L`.
* Another named link to [MarkItDown](http://www.markitdown.net/)
* Sometimes you just want a URL like <http://www.markitdown.net/>.

## Horizontal rule

A horizontal rule is a line that goes across the middle of the page.

---

It''s sometimes handy for breaking things up.

## Images

![Cat](http://lorempixel.com/400/400/cats)

## Code

```haskell
quicksort :: (Ord a) => [a] -> [a]
quicksort [] = []
quicksort (x:xs) =
    let smallerSorted = quicksort [a | a <- xs, a <= x]
        biggerSorted = quicksort [a | a <- xs, a > x]
    in  smallerSorted ++ [x] ++ biggerSorted
```

## Finally


  This is the XSS test:
  <script>alert("xss")</script>
  [XSS](javascript&#58this;alert(1&#41;)
  [XSS](javascript&#58;alert(1&#41;)
  ![XSS](javascript:alert())


  This is the linkify test: https://www.google.com
  And this is the normal link test: [Link](https://www.google.com)
');

UPDATE public.ticket
SET description_comment_id = '00000000-0004-0000-0000-000000000007'
WHERE id = '00000000-0003-0000-0000-000000000006';


INSERT INTO public.comment (id, user_id, ticket_id, create_time, text) VALUES
  ('00000000-0004-0000-0000-000000000008', '660f2968-aa46-4870-bcc5-a3805366cff2',
   '00000000-0003-0000-0000-000000000006', '2016-11-16 20:09:59.019000', 'There is still so much todo');

COMMIT;

-- BEGIN Role Based Ticket Testdata DO NOT ALTER
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
-- END Role Based Ticket Testdata DO NOT ALTER

--TICKET TAG GROUPS
BEGIN;

INSERT INTO public.ticket_tag_group (id, project_id, default_ticket_tag_id, name, exclusive)
VALUES ('00000000-0009-0000-0000-000000000001', '00000000-0002-0000-0000-000000000001', NULL, 'Agile', TRUE);
INSERT INTO public.ticket_tag_group (id, project_id, default_ticket_tag_id, name, exclusive)
VALUES ('00000000-0009-0000-0000-000000000002', '00000000-0002-0000-0000-000000000001', NULL, 'Priority', TRUE);
INSERT INTO public.ticket_tag_group (id, project_id, default_ticket_tag_id, name, exclusive)
VALUES ('00000000-0009-0000-0000-000000000003', '00000000-0002-0000-0000-000000000002', NULL, 'Test', FALSE);

--TICKET TAGS

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES
  ('00000000-0005-0000-0000-000000000001', '00000000-0009-0000-0000-000000000001', 'Feature', 'feature', '008000', 1);

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES ('00000000-0005-0000-0000-000000000002', '00000000-0009-0000-0000-000000000001', 'Bug', 'bug', 'FF0000', 2);

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES ('00000000-0005-0000-0000-000000000003', '00000000-0009-0000-0000-000000000001', 'Implementing', 'implementing',
        'FFA500', 3);

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES
  ('00000000-0005-0000-0000-000000000004', '00000000-0009-0000-0000-000000000001', 'Review', 'review', '008000', 4);


INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES ('00000000-0005-0000-0001-000000000001', '00000000-0009-0000-0000-000000000002', 'Low', 'low', '008000', 5);

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES
  ('00000000-0005-0000-0001-000000000002', '00000000-0009-0000-0000-000000000002', 'Medium', 'medium', 'FFA500', 6);

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES ('00000000-0005-0000-0001-000000000003', '00000000-0009-0000-0000-000000000002', 'High', 'high', 'FF0000', 7);


INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES ('00000000-0005-0000-0002-000000000001', '00000000-0009-0000-0000-000000000003', 'Blue', 'blue', 'FF0000', 7);

INSERT INTO public.ticket_tag (id, ticket_tag_group_id, name, normalized_name, color, "order")
VALUES ('00000000-0005-0000-0002-000000000002', '00000000-0009-0000-0000-000000000003', 'Red', 'red', 'FFFF00', 8);

UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0000-000000000001'
WHERE id = '00000000-0009-0000-0000-000000000001';
UPDATE public.ticket_tag_group
SET default_ticket_tag_id = '00000000-0005-0000-0001-000000000001'
WHERE id = '00000000-0009-0000-0000-000000000002';

--TICKET TAG TICKET
INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000001', '00000000-0005-0000-0000-000000000001');


INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000001', '00000000-0005-0000-0000-000000000003');

INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000002', '00000000-0005-0000-0000-000000000002');

INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000003', '00000000-0005-0000-0000-000000000001');
INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000003', '00000000-0005-0000-0000-000000000002');
INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000003', '00000000-0005-0000-0000-000000000004');


INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000004', '00000000-0005-0000-0000-000000000001');


INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000006', '00000000-0005-0000-0000-000000000001');
INSERT INTO public.assigned_ticket_tag (ticket_id, ticket_tag_id)
VALUES ('00000000-0003-0000-0000-000000000006', '00000000-0005-0000-0001-000000000002');

COMMIT;

BEGIN;
--Assignment-Tag

INSERT INTO public.assignment_tag (id, project_id, name, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000001', '00000000-0002-0000-0000-000000000001', 'Implementing', 'implementing',
        '0000ff');

INSERT INTO public.assignment_tag (id, project_id, name, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000002', '00000000-0002-0000-0000-000000000001', 'Testing', 'testing', '00ff00');

INSERT INTO public.assignment_tag (id, project_id, name, normalized_name, color)
VALUES
  ('00000000-0006-0000-0000-000000000003', '00000000-0002-0000-0000-000000000001', 'Bug Fixing', 'bugfixing', 'ff0000');

INSERT INTO public.assignment_tag (id, project_id, name, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000004', '00000000-0002-0000-0000-000000000001', 'Ticket Owner', 'ticketowner',
        'ff0000');

INSERT INTO public.assignment_tag (id, project_id, name, normalized_name, color)
VALUES
  ('00000000-0006-0000-0000-000000000005', '00000000-0002-0000-0000-000000000001', 'Document', 'document', 'ff0000');

INSERT INTO public.assignment_tag (id, project_id, name, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000006', '00000000-0002-0000-0000-000000000001', 'Review', 'review', 'ff0000');

--BEGIN Assignment-Tag Data for rolebased testing DO NOT ALTER
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000101', '00000000-0002-0000-0000-000000000101', 'dev', 'dev', 'ff0000');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000102', '00000000-0002-0000-0000-000000000101', 'test', 'test', '0000ff');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000103', '00000000-0002-0000-0000-000000000101', 'rev', 'rev', 'ff000f');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000104', '00000000-0002-0000-0000-000000000102', 'dev', 'dev', 'ff0000');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000105', '00000000-0002-0000-0000-000000000102', 'test', 'test', '0000ff');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000106', '00000000-0002-0000-0000-000000000102', 'rev', 'rev', 'ff000f');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000107', '00000000-0002-0000-0000-000000000103', 'dev', 'dev', 'ff0000');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000108', '00000000-0002-0000-0000-000000000103', 'test', 'test', '0000ff');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000109', '00000000-0002-0000-0000-000000000103', 'rev', 'rev', 'ff000f');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000110', '00000000-0002-0000-0000-000000000104', 'dev', 'dev', 'ff0000');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000111', '00000000-0002-0000-0000-000000000104', 'test', 'test', '0000ff');
INSERT INTO PUBLIC.assignment_tag (id, project_id, NAME, normalized_name, color)
VALUES ('00000000-0006-0000-0000-000000000112', '00000000-0002-0000-0000-000000000104', 'rev', 'rev', 'ff000f');
--END Assignment-Tag Data for rolebased testing DO NOT ALTER

--TICKET USER
INSERT INTO public.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000001', '00000000-0006-0000-0000-000000000001',
        '660f2968-aa46-4870-bcc5-a3805366cff2');

INSERT INTO public.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000002', '00000000-0006-0000-0000-000000000001',
        '660f2968-aa46-4870-bcc5-a3805366cff2');

INSERT INTO public.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000003', '00000000-0006-0000-0000-000000000001',
        '660f2968-aa46-4870-bcc5-a3805366cff2');

INSERT INTO public.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000004', '00000000-0006-0000-0000-000000000001',
        '660f2968-aa46-4870-bcc5-a3805366cff2');

INSERT INTO public.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000005', '00000000-0006-0000-0000-000000000001',
        '93ef43d9-20b7-461a-b960-2d1e89ba099f');
INSERT INTO public.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000005', '00000000-0006-0000-0000-000000000002',
        '660f2968-aa46-4870-bcc5-a3805366cff2');

INSERT INTO public.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000006', '00000000-0006-0000-0000-000000000001',
        '93ef43d9-20b7-461a-b960-2d1e89ba099f');
INSERT INTO public.assigned_ticket_user (ticket_id, assignment_tag_id, user_id)
VALUES ('00000000-0003-0000-0000-000000000006', '00000000-0006-0000-0000-000000000002',
        '93ef43d9-20b7-461a-b960-2d1e89ba099f');

--BEGIN ASSIGNED TICKET USERS role based Test data DO NOT ALTER
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
--Ticket 00000000-0003-0000-0000-000000000103 reserved for insert tests
--END ASSIGNED TICKET USERS role based Test data DO NOT ALTER

--Time Category

INSERT INTO public.time_category (id, project_id, name, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000001', '00000000-0002-0000-0000-000000000001', 'Implementing', 'implementing');

INSERT INTO public.time_category (id, project_id, name, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000002', '00000000-0002-0000-0000-000000000001', 'Meeting', 'meeting');

INSERT INTO public.time_category (id, project_id, name, normalized_name)
VALUES ('00000000-0007-0000-0000-000000000003', '00000000-0002-0000-0000-000000000001', 'Testing', 'testing');

--BEGIN TimeCategories for Role based testing DO NOT ALTER
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
--END TimeCategories for Role based testing DO NOT ALTER


--Time

INSERT INTO public.logged_time (id, comment_id, category_id, time)
VALUES ('00000000-0008-0000-0000-000000000001', '00000000-0004-0000-0000-000000000008',
        '00000000-0007-0000-0000-000000000001', 10);


INSERT INTO public.logged_time (id, comment_id, category_id, time)
VALUES ('00000000-0008-0000-0000-000000000002', '00000000-0004-0000-0000-000000000008',
        '00000000-0007-0000-0000-000000000002', 20);

INSERT INTO public.logged_time (id, comment_id, category_id, time)
VALUES ('00000000-0008-0000-0000-000000000003', '00000000-0004-0000-0000-000000000008',
        '00000000-0007-0000-0000-000000000003', 30);
COMMIT;
