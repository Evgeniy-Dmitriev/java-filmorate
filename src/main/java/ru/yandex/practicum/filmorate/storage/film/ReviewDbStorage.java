package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Review> findAllReviews() {
        String sql = "SELECT r.*, IFNULL(SUM(rl.is_like),0) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON r.id = rl.review_id " +
                "GROUP BY r.id " +
                "ORDER BY useful DESC";
        return jdbcTemplate.query(sql, this::mapRowToReview);
    }

    public List<Review> findReviewsByFilmId(int filmId, int count) {
        String sql = "SELECT r.*, IFNULL(SUM(rl.is_like),0) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON r.id = rl.review_id " +
                "WHERE r.film_id = ?" +
                "GROUP BY r.id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId, count);
    }

    public Optional<Review> findReviewById(Long id) {
        String sql = "SELECT r.*, IFNULL(SUM(rl.is_like),0) AS useful " +
                "FROM reviews AS r " +
                "LEFT JOIN reviews_likes AS rl ON r.id = rl.review_id " +
                "WHERE r.id = ?" +
                "GROUP BY r.id " +
                "ORDER BY useful DESC";
        try {
            Review review = jdbcTemplate.queryForObject(sql, this::mapRowToReview, id);
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Review createReview(Review review) {
        validateReview(review);

        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return review;
    }

    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";

        int rowsUpdated = jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не удалось обновить данные для отзыва с ID " + review.getReviewId());
        }
        return findReviewById(review.getReviewId())
                .orElseThrow(() -> new NotFoundException("Не удалось обновить данные для отзыва с ID " + review.getReviewId()));
    }

    public boolean deleteReview(Long reviewId) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        return jdbcTemplate.update(sql, reviewId) > 0;
    }

    public void addLike(int reviewId, int userId) {
        removeLikeOrDislike(reviewId, userId);
        String likeSql = "INSERT INTO reviews_likes (review_id, user_id, is_like) VALUES (?, ?, 1)";
        jdbcTemplate.update(likeSql, reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        removeLikeOrDislike(reviewId, userId);
        String dislikeSql = "INSERT INTO reviews_likes (review_id, user_id, is_like) VALUES (?, ?, -1)";
        jdbcTemplate.update(dislikeSql, reviewId, userId);
    }

    public void removeLike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? AND is_like = 1";
        jdbcTemplate.update(sql, reviewId, userId);

    }

    public void removeDislike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? AND is_like = -1";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getLong("id"));
        review.setContent(resultSet.getString("content"));
        review.setIsPositive(resultSet.getBoolean("is_positive"));
        review.setUserId(resultSet.getLong("user_id"));
        review.setFilmId(resultSet.getLong("film_id"));
        review.setUseful(resultSet.getInt("useful"));

        return review;
    }

    private void validateReview(Review review) {
        if (review.getFilmId() < 0 || review.getUserId() < 0) {
            throw new NotFoundException("ID должны быть положительными числами");
        }
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new ValidationException("Не указаны user_id и/или film_id");
        }
    }

    private void removeLikeOrDislike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }
}
