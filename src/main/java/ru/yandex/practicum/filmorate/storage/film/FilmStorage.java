package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film saveFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteFilmById(Long id);

    Optional<Film> findFilmById(Long filmId);

    void addLike(Film film, Long userId);

    void removeLike(Film film, Long userId);

    List<Film> findMostPopularFilms(int count);

    boolean hasFilmsId(Long filmId);
}
