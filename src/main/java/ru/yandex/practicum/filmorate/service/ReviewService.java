package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.ReviewDbStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;

    @Autowired
    public ReviewService(ReviewDbStorage reviewDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
    }

    public List<Review> getAllReviews() {
        return reviewDbStorage.findAllReviews();
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        return reviewDbStorage.findReviewsByFilmId(filmId, count);
    }

    public Review getReviewById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Неверный id отзыва");
        return reviewDbStorage.findReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
    }

    public Review createReview(Review review) {
        return reviewDbStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        return reviewDbStorage.updateReview(review);
    }

    public boolean deleteReview(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Отзыв с id = " + id + " не найден");
        return reviewDbStorage.deleteReview(id);
    }

    public Review addLike(int reviewId, int userId) {
        reviewDbStorage.addLike(reviewId, userId);
        return getReviewById((long) reviewId);
    }

    public Review addDislike(int reviewId, int userId) {
        reviewDbStorage.addDislike(reviewId, userId);
        return getReviewById((long) reviewId);
    }

    public Review removeLike(int reviewId, int userId) {
        reviewDbStorage.removeLike(reviewId, userId);
        return getReviewById((long) reviewId);
    }

    public Review removeDislike(int reviewId, int userId) {
        reviewDbStorage.removeDislike(reviewId, userId);
        return getReviewById((long) reviewId);
    }


}
