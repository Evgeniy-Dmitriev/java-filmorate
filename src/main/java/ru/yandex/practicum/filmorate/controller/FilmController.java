package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storages;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage storage = Storages.getFilmStorage();

    @GetMapping
    public Collection<Film> findAll() {
        return storage.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return storage.postFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
       return storage.putFilm(newFilm);
    }
}
