DROP TABLE IF EXISTS films, mpa, genres, film_genres, users, likes, friends, directors, film_directors;

CREATE TABLE IF NOT EXISTS ratings (
        rating_id BIGINT NOT NULL AUTO_INCREMENT,
        name VARCHAR(50) NOT NULL,
        CONSTRAINT rating_pk PRIMARY KEY (rating_id)
);

CREATE TABLE IF NOT EXISTS films (
        film_id BIGINT NOT NULL AUTO_INCREMENT,
        name VARCHAR(50) NOT NULL,
        description VARCHAR(255),
        release_date DATE,
        duration INTEGER,
        rating_id bigint REFERENCES ratings (rating_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
        CONSTRAINT films_pk PRIMARY KEY (film_id)
);

CREATE TABLE IF NOT EXISTS genres (
        genre_id BIGINT NOT NULL AUTO_INCREMENT,
        name VARCHAR(50) NOT NULL,
        CONSTRAINT genre_pk PRIMARY KEY (genre_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
        film_id bigint REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE,
        genre_id bigint REFERENCES genres (genre_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
        CONSTRAINT film_genres_pk PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
        user_id BIGINT NOT NULL AUTO_INCREMENT,
        email VARCHAR(50) NOT NULL,
        login VARCHAR(50) NOT NULL,
        name VARCHAR(50) NOT NULL,
        birthday DATE,
        CONSTRAINT users_pk PRIMARY KEY (user_id),
        CONSTRAINT users_unique_email UNIQUE (email),
        CONSTRAINT users_unique_login UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS likes (
        film_id BIGINT REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE RESTRICT,
        user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE RESTRICT,
        CONSTRAINT likes_pk PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friends (
        user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE RESTRICT,
        friend_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE RESTRICT,
        CONSTRAINT friends_pk PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT director_pk PRIMARY KEY (director_id)
);

CREATE TABLE IF NOT EXISTS film_directors (
film_id bigint REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE,
director_id bigint REFERENCES directors (director_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT film_director_pk PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(1024),
    is_positive BOOLEAN,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
    useful INT,
    CONSTRAINT reviews_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reviews_likes (
    review_id BIGINT REFERENCES reviews(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT reviews_likes_pk PRIMARY KEY (review_id, user_id)
);