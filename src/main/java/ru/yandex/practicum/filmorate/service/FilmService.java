package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserService userService;
    private final RatingService ratingService;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       UserService userService,
                       RatingService ratingService,
                       GenreService genreService,
                       DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userService = userService;
        this.ratingService = ratingService;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film createFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            String message = "Название не может быть пустым";
            log.error("Ошибка при добавлении фильма: {}", message);
            throw new ValidationException(message);
        }
        validate(film);
        ratingService.exists(film);
        genreService.exists(film);
        return filmStorage.saveFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            String message = "Id должен быть указан";
            log.error("Ошибка при обновлении фильма: {}", message);
            throw new ConditionsNotMetException(message);
        }
        if (filmStorage.hasFilmsId(newFilm.getId())) {
            validate(newFilm);
            ratingService.exists(newFilm);
            genreService.exists(newFilm);
            return filmStorage.updateFilm(newFilm);
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public boolean deleteFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Фильм с id = " + id + " не найден");
        return filmStorage.deleteFilmById(id);
    }

    public Film getFilmById(Long id) {
        return filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        filmStorage.addLike(film, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        filmStorage.removeLike(film, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findMostPopularFilms(count);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Пользователь и друг не могут быть одним и тем же человеком.");
        }
        userService.getUserById(userId);
        userService.getUserById(friendId);

        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);

        return commonFilms != null ? commonFilms : Collections.emptyList();
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

    public List<Film> getDirectorFilms(Long directorId, String sortBy) {
        List<Film> result;

        switch (sortBy) {
            case "year", "likes" -> result = filmStorage.getByDirector(directorId, sortBy);
            default -> {
                log.info("Попытка получить список фильмов по режиссёру с sortBy = {}", sortBy);
                throw new NotFoundException("Был передан sortBy с неподдерживаемым типом сортировки: " + sortBy +
                        ". Поддерживаются только year, likes");
            }
        }

        return result;
    }
}
