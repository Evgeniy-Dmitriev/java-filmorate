package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.RatingDbStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingDbStorage ratingDbStorage;

    @Autowired
    public RatingService(RatingDbStorage ratingDbStorage) {
        this.ratingDbStorage = ratingDbStorage;
    }

    public List<Rating> getAllRatings() {
        return ratingDbStorage.findAllRatings().stream()
                .filter(Objects::nonNull)
                .toList();
    }

    public Rating getRatingById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Неверный id рейтинга");
        return ratingDbStorage.findRatingById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
    }
}
