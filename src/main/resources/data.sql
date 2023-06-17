INSERT INTO PUBLIC.MPA
SELECT 1, 'G'
where not exists(select * from PUBLIC.MPA where MPA_ID = 1);

INSERT INTO PUBLIC.MPA
SELECT 2, 'PG'
where not exists(select * from PUBLIC.MPA where MPA_ID = 2);

INSERT INTO PUBLIC.MPA
SELECT 3, 'PG-13'
where not exists(select * from PUBLIC.MPA where MPA_ID = 3);

INSERT INTO PUBLIC.MPA
SELECT 4, 'R'
where not exists(select * from PUBLIC.MPA where MPA_ID = 4);

INSERT INTO PUBLIC.MPA
SELECT 5, 'NC-17'
where not exists(select * from PUBLIC.MPA where MPA_ID = 5);

INSERT INTO PUBLIC.GENRES
SELECT 1, 'Комедия'
where not exists(select * from PUBLIC.GENRES  where GENRE_ID = 1);

INSERT INTO PUBLIC.GENRES
SELECT 2, 'Драма'
where not exists(select * from PUBLIC.GENRES  where GENRE_ID = 2);

INSERT INTO PUBLIC.GENRES
SELECT 3, 'Мультфильм'
where not exists(select * from PUBLIC.GENRES  where GENRE_ID = 3);

INSERT INTO PUBLIC.GENRES
SELECT 4, 'Триллер'
where not exists(select * from PUBLIC.GENRES  where GENRE_ID = 4);

INSERT INTO PUBLIC.GENRES
SELECT 5, 'Документальный'
where not exists(select * from PUBLIC.GENRES  where GENRE_ID = 5);

INSERT INTO PUBLIC.GENRES
SELECT 6, 'Боевик'
where not exists(select * from PUBLIC.GENRES  where GENRE_ID = 6);

DELETE FROM MPA_FILM;
DELETE FROM GENRES_FILM;
DELETE FROM FILMLIKES;
DELETE FROM FILMS;
ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1;

DELETE FROM FRIENDS;
DELETE FROM FILMLIKES;
DELETE FROM USERS;
ALTER TABLE  USERS  ALTER COLUMN USER_ID  RESTART WITH 1;
