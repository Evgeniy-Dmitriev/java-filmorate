package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.findAllGenres().stream()
                .filter(Objects::nonNull)
                .toList();
    }

    public Genre getGenreById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Неверный id жанра");
        return genreDbStorage.findGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }

    public void exists(Film film) {
        Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) return;
        for (Genre genre : genres) {
           Genre genre1 = getGenreById(genre.getId());
           if (genre1.getName() == null || genre1.getName().isEmpty()) {
               throw new NotFoundException("Жанр с идентификатором не найден.");
           }
        }

    }
}
