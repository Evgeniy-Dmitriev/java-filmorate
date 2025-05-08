package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorDbStorage;

import java.util.List;
import java.util.Objects;

@Service
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;


    @Autowired
    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }


    public Director getDirectorById(Long id) {
        return directorDbStorage.findDirectorById(id).orElseThrow(() -> new NotFoundException("Директор с id " + id + " не найден"));
    }

    public Director createDirector(Director director) {
        return directorDbStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDbStorage.updateDirector(director);
    }

    public boolean deleteDirector(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Директор с id = " + id + " не найден");
        return directorDbStorage.deleteDirector(id);
    }

    public List<Director> getAllDirectors() {
        return directorDbStorage.findAllDirectors().stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
