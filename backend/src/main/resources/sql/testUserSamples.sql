BEGIN;
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, disabled) VALUES
  ('00000000-0001-0000-0000-000000000001', 'user_a', 'a@a.a', 'Mr. A',
   '$2a$10$mTEkiQq2Wo./aqfekJHPk.5sG8JLWqWYbtMODwk9xQwQp0GtkCiM.', 'ADMIN', '00000000-0001-0000-0000-abcdef123641', FALSE); --aaaa
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, disabled) VALUES
  ('00000000-0001-0000-0000-000000000002', 'user_b', 'b@b.b', 'Berta Berta',
   '$2a$10$Ydzo0FR5x8ZweeaeIQS2gevmLqsZuS37.bWRYy.f.u62NG00MAOcS', 'USER', '00000000-0001-0000-2343-abcdef123641', FALSE); --bbbb
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, disabled) VALUES
  ('00000000-0001-0000-0000-000000000003', 'user_c', 'c@c.c', 'Gaius Iulius Caesar',
   '$2a$10$OgvbSbiDxizgC/6K3dhVwO8iY6.QFS6f2PvE1AyJS1Vmo6Rnb3Gve', 'OBSERVER', '00000000-0001-8676-0000-abcdef123641', FALSE); --cccc
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, disabled) VALUES
  ('00000000-0000-0000-0000-000000000000', 'admin', 'admin@admin.invalid', 'Admin',
   '$2a$10$dXjkyD704.vNyYWrsmEbrewcMeWIz1fDcjVVuggUyLmExGQQD3RGC', 'ADMIN', '9a030c2e-b2c7-4d98-825b-92c148897f4a', FALSE);
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, disabled) VALUES
  ('660f2968-aa46-4870-bcc5-a3805366cff2', 'drasko', 'stefan.draskovits@test.at', 'Stefan Draskovits',
   '$2a$10$NuX1RqGiFg38qjF75b88J.oWw271xVYhsPvLRxHAQHnS2V9i0nNza', 'ADMIN', '4aa33174-bdf2-4d33-b80f-d7fb8d121923', FALSE); --stefan-supersecure
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, disabled) VALUES
  ('93ef43d9-20b7-461a-b960-2d1e89ba099f', 'heinzl', 'michael.heinzl@test.de', 'Michael Heinzl',
   '$2a$10$.dLg4Vgt7JrP.564p/tPQOm.TLoy3HieFP1ZpnyWVPkJDYrG6r.Ce', 'OBSERVER', '370f4e86-1ebf-4b70-a113-add96d0905e1', FALSE); --michael-supersecure
INSERT INTO public."user" (id, username, mail, name, password_hash, role, current_token, disabled) VALUES
  ('00000000-0001-0000-0000-000000000004', 'cannot', 'adsf@asdf.ad', 'I Can Nothing the Second',
   '$2a$10$mTEkiQq2Wo./aqfekJHPk.5sG8JLWqWYbtMODwk9xQwQp0GtkCiM.', 'USER', '4aa33174-0001-4d33-0000-add96d0905e1', FALSE); --aaaa

INSERT INTO "project" VALUES
  ('00000000-0002-0000-0000-000000000001', 'Bitchip', 'Pfizer Consumer Healthcare', '2016-07-03 08:49:05', NULL, NULL, '', FALSE),
  ('00000000-0002-0000-0000-000000000002', 'Veribet', 'H E B', '2016-08-26 21:57:39', NULL, NULL, '', FALSE);
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
   to_date('2016-11-11', 'YYYY-MM-DD'));
COMMIT;