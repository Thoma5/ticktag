begin;

delete from "user";

insert into "user" values
    ('00000000-0001-0000-0000-000000000001', 'a@a.a', 'Mr. A', '$2a$10$mTEkiQq2Wo./aqfekJHPk.5sG8JLWqWYbtMODwk9xQwQp0GtkCiM.', 'ADMIN', '00000000-0001-0000-0000-abcdef123641'), -- aaaa
    ('00000000-0001-0000-0000-000000000002', 'b@b.b', 'Berta Berta', '$2a$10$Ydzo0FR5x8ZweeaeIQS2gevmLqsZuS37.bWRYy.f.u62NG00MAOcS', 'USER', '00000000-0001-0000-2343-abcdef123641'), -- bbbb
    ('00000000-0001-0000-0000-000000000003', 'c@c.c', 'Gaius Iulius Caesar', '$2a$10$OgvbSbiDxizgC/6K3dhVwO8iY6.QFS6f2PvE1AyJS1Vmo6Rnb3Gve', 'OBSERVER', '00000000-0001-8676-0000-abcdef123641'); -- cccc

commit;
