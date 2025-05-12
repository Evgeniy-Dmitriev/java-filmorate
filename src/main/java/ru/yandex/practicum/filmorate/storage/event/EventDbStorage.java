package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeed(Long userId) {
        String sql = "SELECT * FROM feed WHERE user_id = ? ORDER BY event_id ASC";
        return jdbcTemplate.query(sql, this::mapRow, userId);
    }

    @Override
    public void createEvent(Event event) {
        final String sql = "INSERT INTO feed (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );

        log.info("Создано событие: {}", event);
    }

    private Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return Event.builder()
                    .eventId(rs.getLong("event_id"))
                    .timestamp(rs.getLong("timestamp"))
                    .userId(rs.getLong("user_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .operation(EventOperation.valueOf(rs.getString("operation")))
                    .entityId(rs.getLong("entity_id"))
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Ошибка чтения события из базы данных: некорректные enum значения. Строка: {}", rowNum, e);
            throw new SQLException("Некорректные значения enum в строке " + rowNum, e);
        }
    }
}
