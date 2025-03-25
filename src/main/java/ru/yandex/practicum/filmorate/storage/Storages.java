package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

public class Storages {

    private Storages() {
    }

    public static FilmStorage getFilmStorage() {
        return new InMemoryFilmStorage();
    }

    public static UserStorage getUserStorage() {
        return new InMemoryUserStorage();
    }
}
