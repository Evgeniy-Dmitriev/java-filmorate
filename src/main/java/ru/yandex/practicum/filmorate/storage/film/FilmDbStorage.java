package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sql = "SELECT f.*, r.rating_id, r.name as mpa_name " +
                "FROM films f " +
                "JOIN ratings r ON f.rating_id = r.rating_id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);

        // Загружаем жанры и лайки для всех фильмов
        for (Film film : films) {
            loadFilmGenres(film);
            loadFilmLikes(film);
            loadFilmDirectors(film);
        }

        return films;
    }

    @Override
    public Film saveFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));

            ps.setLong(4, film.getDuration().toMillis());

            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            System.out.println("Директор фильма из ввода " + film.getDirectors());
            saveFilmDirectors(film);
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, rating_id = ? WHERE film_id = ?";

        int rowsAffected = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMillis(),
                film.getMpa().getId(),
                film.getId());

        if (rowsAffected == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            saveFilmDirectors(film);
        }

        return findFilmById(film.getId()).orElse(film);
    }

    @Override
    public boolean deleteFilmById(Long id) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", id);

        String sql = "DELETE FROM films WHERE film_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        String sql = "SELECT f.*, r.rating_id, r.name as mpa_name " +
                "FROM films f " +
                "JOIN ratings r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
            if (film != null) {
                loadFilmGenres(film);
                loadFilmLikes(film);
                loadFilmDirectors(film);
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(Film film, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, film.getId(), userId);
    }

    @Override
    public void removeLike(Film film, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, film.getId(), userId);
    }

    @Override
    public List<Film> findPopularFilms(Integer count, Long genreId, Integer year) {
        String findPopularFilmsByGenreAndYear = "SELECT f.*, r.rating_id, r.name as mpa_name, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) as likes_count " +
                "FROM films f " +
                "JOIN ratings r ON f.rating_id = r.rating_id " +
                "JOIN film_genres fg ON f.film_id = fg.film_id " +
                "WHERE fg.genre_id = ? AND YEAR(f.release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        String findPopularFilmsByGenre = "SELECT f.*, r.rating_id, r.name as mpa_name, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) as likes_count " +
                "FROM films f " +
                "JOIN ratings r ON f.rating_id = r.rating_id " +
                "JOIN film_genres fg ON f.film_id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        String findPopularFilmsByYear = "SELECT f.*, r.rating_id, r.name as mpa_name, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) as likes_count " +
                "FROM films f " +
                "JOIN ratings r ON f.rating_id = r.rating_id " +
                "JOIN film_genres fg ON f.film_id = fg.film_id " +
                "WHERE YEAR(f.release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        String findPopularFilmsByLikes = "SELECT f.*, r.rating_id, r.name as mpa_name, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) as likes_count " +
                "FROM films f " +
                "JOIN ratings r ON f.rating_id = r.rating_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        List<Film> films = new ArrayList<>();


        if (genreId != null && year != null) {
            films = jdbcTemplate.query(findPopularFilmsByGenreAndYear, this::mapRowToFilm, genreId, year, count);
        } else if (genreId != null) {
            films = jdbcTemplate.query(findPopularFilmsByGenre, this::mapRowToFilm, genreId, count);
        } else if (year != null) {
            films = jdbcTemplate.query(findPopularFilmsByYear, this::mapRowToFilm, year, count);
        } else {
            // в случае, если фильтры не указаны, возвращаем общий список популярных фильмов
            films = jdbcTemplate.query(findPopularFilmsByLikes, this::mapRowToFilm, count);
        }

        for (Film film : films) {
            loadFilmGenres(film);
            loadFilmLikes(film);
            loadFilmDirectors(film);
        }

        return films;
    }

    @Override
    public boolean hasFilmsId(Long filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count != null && count > 0;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = "SELECT f.*, r.rating_id, r.name as mpa_name, " +
                "COUNT(DISTINCT l.user_id) as rate " +
                "FROM films f " +
                "JOIN likes l1 ON f.film_id = l1.film_id AND l1.user_id = ? " +
                "JOIN likes l2 ON f.film_id = l2.film_id AND l2.user_id = ? " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN ratings r ON f.rating_id = r.rating_id " +
                "GROUP BY f.film_id " +
                "ORDER BY rate DESC";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, userId, friendId);
        for (Film film : films) {
            loadFilmGenres(film);
            loadFilmLikes(film);
        }

        return films;
    }

    @Override
    public List<Film> getByDirector(Long directorId, String sortBy) {
        String getByYear = "SELECT f.film_id FROM films f " +
                "INNER JOIN film_directors fd ON fd.film_id = f.film_id " +
                "WHERE fd.director_id = ? ORDER BY EXTRACT(YEAR FROM f.release_date)";

        String getByLikes = "SELECT f.film_id FROM films f " +
                "INNER JOIN film_directors fd ON fd.film_id = f.film_id " +
                "LEFT JOIN likes l ON l.film_id = f.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC";

        List<Long> filmIds = switch (sortBy) {
            case "year" -> jdbcTemplate.queryForList(getByYear, Long.class, directorId);
            case "likes" -> jdbcTemplate.queryForList(getByLikes, Long.class, directorId);
            default -> new ArrayList<>();
        };

        List<Film> result = new ArrayList<>();

        for (Long id : filmIds) {
            result.add(findFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден")));
        }

        return result;
    }

    @Override
    public Set<Long> findFilmLikes(User user) {
        String sql = "SELECT film_id FROM likes WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, user.getId()));
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        int durationMillis = resultSet.getInt("duration");
        film.setDuration(Duration.ofMillis(durationMillis));

        Rating mpa = new Rating();
        mpa.setId(resultSet.getLong("rating_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    }

    private void saveFilmGenres(Film film) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        List<Genre> genres = new ArrayList<>(film.getGenres());

        for (Genre genre : genres) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    private void saveFilmDirectors(Film film) {
        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?,?)";

        List<Director> directors = new ArrayList<>(film.getDirectors());
        for (Director director : directors) {
            jdbcTemplate.update(sql, film.getId(), director.getId());
        }
    }

    private void loadFilmGenres(Film film) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";

        List<Genre> genres = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(resultSet.getLong("genre_id"));
            genre.setName(resultSet.getString("name"));
            return genre;
        }, film.getId());

        film.setGenres(new HashSet<>(genres));
    }

    private void loadFilmLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(sql, Long.class, film.getId());
        film.setLikes(new HashSet<>(likes));
    }

    private void loadFilmDirectors(Film film) {
        String sql = "SELECT d.director_id, d.name " +
                "FROM directors AS d " +
                "JOIN film_directors AS fd ON d.director_id = fd.director_id " +
                "WHERE film_id = ?";

        List<Director> directors = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            Director director = new Director();
            director.setId(resultSet.getLong("director_id"));
            director.setName(resultSet.getString("name"));
            return director;
        }, film.getId());

        film.setDirectors(new HashSet<>(directors));
    }
}
