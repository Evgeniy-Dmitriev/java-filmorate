package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            String message = "Название не может быть пустым";
            log.error("Ошибка при добавлении фильма: {}", message);
            throw new ValidationException(message);
        }
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() <= 0) {
            String message = "Id должен быть указан";
            log.error("Ошибка при обновлении фильма: {}", message);
            throw new ConditionsNotMetException(message);
        }
        if (films.containsKey(newFilm.getId())) {
            validate(newFilm);
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Фильм обновлён: {}", oldFilm);
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private void validate(Film film) {
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String message = "Максимальная длина описания — 200 символов";
            log.error("Ошибка при валидации фильма: {}", message);
            throw new ValidationException(message);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String message = "Дата релиза — не раньше 28 декабря 1895 года";
            log.error("Ошибка при валидации фильма: {}", message);
            throw new ValidationException(message);
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            String message = "Продолжительность фильма должна быть положительным числом";
            log.error("Ошибка при валидации фильма: {}", message);
            throw new ValidationException(message);
        }
        log.debug("Валидация фильма прошла успешно: {}", film.getName());
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
