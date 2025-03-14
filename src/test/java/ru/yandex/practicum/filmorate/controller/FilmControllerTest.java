package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
        filmController = new FilmController();
    }

    @Test
    void testFindAll() {
        Film film = Film.builder()
                .name("Интерстеллар")
                .description("Научно-фантастический фильм")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .duration(Duration.ofMinutes(169))
                .build();

        filmController.create(film);
        List<Film> films = new ArrayList<>(filmController.findAll());

        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals("Интерстеллар", films.get(0).getName());
    }

    @Test
    void testAddValidFilm() {
        Film film = Film.builder()
                .name("Интерстеллар")
                .description("Научно-фантастический фильм")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .duration(Duration.ofMinutes(169))
                .build();

        Film addedFilm = filmController.create(film);

        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
        assertEquals("Интерстеллар", addedFilm.getName());
    }

    @Test
    void testUpdateValidFilm() {
        Film film = Film.builder()
                .name("Интерстеллар")
                .description("Научно-фантастический фильм")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .duration(Duration.ofMinutes(169))
                .build();

        Film addedFilm = filmController.create(film);

        Film film2 = Film.builder()
                .id(1)
                .name("Интерстеллар 2")
                .description("Научно-фантастический фильм 2")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .duration(Duration.ofMinutes(169))
                .build();

        filmController.update(film2);

        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
        assertEquals("Интерстеллар 2", addedFilm.getName());
        assertEquals("Научно-фантастический фильм 2", addedFilm.getDescription());
    }

    @Test
    void testEmptyName() {
        Film film = Film.builder()
                .name("")
                .description("Тестовое описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertTrue(exception.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    void testNullName() {
        Film film = Film.builder()
                .name(null)
                .description("Тестовое описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertTrue(exception.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    void testDescriptionMaxLength() {
        Film film = Film.builder()
                .name("Тестовое название")
                .description("А".repeat(201))
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertTrue(exception.getMessage().contains("Максимальная длина описания — 200 символов"));
    }

    @Test
    void testDescriptionExactlyMaxLength() {
        Film film = Film.builder()
                .name("Тестовое название")
                .description("А".repeat(200))
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        Film addedFilm = filmController.create(film);

        assertNotNull(addedFilm);
        assertEquals(200, addedFilm.getDescription().length());
    }

    @Test
    void testReleaseDate() {
        Film film = Film.builder()
                .name("Тестовое название")
                .description("Тестовое описание")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(Duration.ofMinutes(120))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertTrue(exception.getMessage().contains("Дата релиза — не раньше 28 декабря 1895 года"));
    }

    @Test
    void testExactlyEarliestReleaseDate() {
        Film film = Film.builder()
                .name("Тестовое название")
                .description("Тестовое описание")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(Duration.ofMinutes(120))
                .build();

        Film addedFilm = filmController.create(film);

        assertNotNull(addedFilm);
        assertEquals(LocalDate.of(1895, 12, 28), addedFilm.getReleaseDate());
    }

    @Test
    void testNegativeDuration() {
        Film film = Film.builder()
                .name("Тестовое название")
                .description("Тестовое описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(Duration.ofMinutes(-1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительным числом"));
    }

    @Test
    void testZeroDuration() {
        Film film = Film.builder()
                .name("Тестовое название")
                .description("Тестовое описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(Duration.ZERO)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });

        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительным числом"));
    }


    @Test
    void testUpdateNonExistingFilm() {
        Film film = Film.builder()
                .id(999)
                .name("Тестовое название")
                .description("Тестовое описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(Duration.ZERO)
                .build();

        Exception exception = assertThrows(NotFoundException.class, () -> {
            filmController.update(film);
        });

        assertTrue(exception.getMessage().contains("Фильм с id = " + film.getId() + " не найден"));
    }
}