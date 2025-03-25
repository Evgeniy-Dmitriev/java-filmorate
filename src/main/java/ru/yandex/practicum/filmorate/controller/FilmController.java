package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmStorage.postFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
       return filmStorage.putFilm(newFilm);
    }
}
