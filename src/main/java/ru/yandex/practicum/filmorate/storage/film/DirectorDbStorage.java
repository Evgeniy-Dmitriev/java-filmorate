package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Director> findAllDirectors() {
        String sql = "SELECT * FROM directors ORDER BY director_id";

        return jdbcTemplate.query(sql, this::mapRowToDirector);
    }

    public Optional<Director> findDirectorById(Long directorId) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToDirector, directorId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Director createDirector(Director director) {
        String sql = "INSERT INTO directors(name) VALUES(?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, director.getName());
            return preparedStatement;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        director.setId(id);
        return director;
    }

    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";

        int rowsUpdated = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не удалось обновить данные для режиссера с ID " + director.getId());
        }
        return findDirectorById(director.getId())
                .orElseThrow(() -> new NotFoundException("Не удалось обновить данные для режиссера с ID " + director.getId()));
    }

    public boolean deleteDirector(Long directorId) {
        String sql = "DELETE FROM directors WHERE director_id = ?";

        return jdbcTemplate.update(sql, directorId) > 0;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getLong("director_id"));
        director.setName(resultSet.getString("name"));
        return director;
    }
}
