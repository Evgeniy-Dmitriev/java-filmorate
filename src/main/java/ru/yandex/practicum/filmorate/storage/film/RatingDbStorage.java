package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Rating> mapper;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Rating> mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    public List<Rating> findAllRatings() {
        String sql = "SELECT * FROM ratings ORDER BY rating_id";
        return jdbcTemplate.query(sql, mapper);
    }

    public Optional<Rating> findRatingById(Long id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        try {
            Rating mpa = jdbcTemplate.queryForObject(sql, mapper, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
