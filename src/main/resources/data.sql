INSERT INTO ratings (rating_id, name)
SELECT 1, 'G'
WHERE NOT EXISTS (
    SELECT 1 FROM ratings WHERE name = 'G'
);

INSERT INTO ratings (rating_id, name)
SELECT 2, 'PG'
WHERE NOT EXISTS (
    SELECT 1 FROM ratings WHERE name = 'PG'
);

INSERT INTO ratings (rating_id, name)
SELECT 3, 'PG-13'
WHERE NOT EXISTS (
    SELECT 1 FROM ratings WHERE name = 'PG-13'
);

INSERT INTO ratings (rating_id, name)
SELECT 4, 'R'
WHERE NOT EXISTS (
    SELECT 1 FROM ratings WHERE name = 'R'
);

INSERT INTO ratings (rating_id, name)
SELECT 5, 'NC-17'
WHERE NOT EXISTS (
    SELECT 1 FROM ratings WHERE name = 'NC-17'
);

INSERT INTO genres (genre_id, name)
SELECT 1, 'Комедия'
WHERE NOT EXISTS (
SELECT 1 FROM genres WHERE name = 'Комедия'
);

INSERT INTO genres (genre_id, name)
SELECT 2, 'Драма'
WHERE NOT EXISTS (
SELECT 1 FROM genres WHERE name = 'Драма'
);

INSERT INTO genres (genre_id, name)
SELECT 3, 'Мультфильм'
WHERE NOT EXISTS (
SELECT 1 FROM genres WHERE name = 'Мультфильм'
);

INSERT INTO genres (genre_id, name)
SELECT 4, 'Триллер'
WHERE NOT EXISTS (
SELECT 1 FROM genres WHERE name = 'Триллер'
);

INSERT INTO genres (genre_id, name)
SELECT 5, 'Документальный'
WHERE NOT EXISTS ( SELECT 1 FROM genres WHERE name = 'Документальный'
);

INSERT INTO genres (genre_id, name)
SELECT 6, 'Боевик'
WHERE NOT EXISTS (
SELECT 1 FROM genres WHERE name = 'Боевик'
);