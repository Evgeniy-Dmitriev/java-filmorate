package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFilmById(@PathVariable Long id) {
        return filmService.deleteFilmById(id);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count,
                                      @RequestParam(name = "genreId", required = false) Long genreId,
                                      @RequestParam(name = "year", required = false) Integer year) {
        List<Film> films;
        if (genreId != null && year != null) {
            films = filmService.getMostPopularFilmsByGenreAndYear(count, genreId, year);
        } else if (genreId != null) {
            films = filmService.getPopularFilmsByGenre(count, genreId);
        } else if (year != null) {
            films = filmService.getPopularFilmsByYear(count, year);
        } else {
            // в случае, если фильтры не указаны, возвращаем общий список популярных фильмов
            films = new ArrayList<>(filmService.getMostPopularFilms(count));
        }

        return films;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getPopularFromDirector(@PathVariable Long directorId, @RequestParam String sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }
}
