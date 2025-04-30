package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage(),
                new RatingService(new RatingDbStorage(new JdbcTemplate())),
                new GenreService(new GenreDbStorage(new JdbcTemplate()))));
    }

    @Test
    void testFindAll() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Научно-фантастический фильм");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(Duration.ofMinutes(169));

        filmController.addFilm(film);
        List<Film> films = new ArrayList<>(filmController.findAll());

        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals("Интерстеллар", films.get(0).getName());
    }

    @Test
    void testAddValidFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Научно-фантастический фильм");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(Duration.ofMinutes(169));

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
        assertEquals("Интерстеллар", addedFilm.getName());
    }

    @Test
    void testUpdateValidFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Научно-фантастический фильм");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(Duration.ofMinutes(169));

        Film addedFilm = filmController.addFilm(film);

        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("Интерстеллар 2");
        film2.setDescription("Научно-фантастический фильм 2");
        film2.setReleaseDate(LocalDate.of(2014, 11, 7));
        film2.setDuration(Duration.ofMinutes(169));

        filmController.update(film2);

        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
        assertEquals("Интерстеллар 2", addedFilm.getName());
        assertEquals("Научно-фантастический фильм 2", addedFilm.getDescription());
    }

    @Test
    void testEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertTrue(exception.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    void testNullName() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertTrue(exception.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    void testDescriptionMaxLength() {
        Film film = new Film();
        film.setName("Тестовое название");
        film.setDescription("А".repeat(201));
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertTrue(exception.getMessage().contains("Максимальная длина описания — 200 символов"));
    }

    @Test
    void testDescriptionExactlyMaxLength() {
        Film film = new Film();
        film.setName("Тестовое название");
        film.setDescription("А".repeat(200));
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(200, addedFilm.getDescription().length());
    }

    @Test
    void testReleaseDate() {
        Film film = new Film();
        film.setName("Тестовое название");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(Duration.ofMinutes(120));

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertTrue(exception.getMessage().contains("Дата релиза — не раньше 28 декабря 1895 года"));
    }

    @Test
    void testExactlyEarliestReleaseDate() {
        Film film = new Film();
        film.setName("Тестовое название");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(Duration.ofMinutes(120));

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(LocalDate.of(1895, 12, 28), addedFilm.getReleaseDate());
    }

    @Test
    void testNegativeDuration() {
        Film film = new Film();
        film.setName("Тестовое название");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ofMinutes(-1));

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительным числом"));
    }

    @Test
    void testZeroDuration() {
        Film film = new Film();
        film.setName("Тестовое название");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ZERO);

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительным числом"));
    }


    @Test
    void testUpdateNonExistingFilm() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Тестовое название");
        film.setDescription("Тестовое описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ZERO);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            filmController.update(film);
        });

        assertTrue(exception.getMessage().contains("Фильм с id = " + film.getId() + " не найден"));
    }
}