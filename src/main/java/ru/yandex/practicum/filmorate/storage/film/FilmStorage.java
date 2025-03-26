package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    // GET "/films"
    Collection<Film> findAllFilms();

    // POST "/films"
    Film saveFilm(Film film);

    // PUT "/films"
    Film putFilm(Film film);

    // GET "/films/{id}
    Optional<Film> findFilmById(Long filmId);

    // PUT /films/{id}/like/{userId}
    void putLike(Film film, Long userId);

    // DELETE /films/{id}/like/{userId}
    void removeLike(Film film, Long userId);

    // GET /films/popular?count={count}
    List<Film> findMostPopularFilms(int count);

    boolean hasFilmsId(Long filmId);
}
