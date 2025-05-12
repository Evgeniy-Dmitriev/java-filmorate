package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film saveFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteFilmById(Long id);

    Optional<Film> findFilmById(Long filmId);

    void addLike(Film film, Long userId);

    void removeLike(Film film, Long userId);

    boolean hasFilmsId(Long filmId);

    Set<Long> findFilmLikes(User user);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getByDirector(Long directorId, String sortBy);

    List<Film> findPopularFilms(Integer count, Long genreId, Integer year);

    List<Film> search(String query, String by);
}
