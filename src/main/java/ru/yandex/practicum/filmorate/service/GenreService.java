package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Optional<Genre> getGenreById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Неверный id жанра");
        return genreDbStorage.findGenreById(id);
    }
}
