package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RatingDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Rating> findAllRatings() {
        String sql = "SELECT * FROM ratings ORDER BY rating_id";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    public Optional<Rating> findRatingById(Long id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        try {
            Rating mpa = jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private Rating mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Rating mpa = new Rating();
        mpa.setId(resultSet.getLong("rating_id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }
}
