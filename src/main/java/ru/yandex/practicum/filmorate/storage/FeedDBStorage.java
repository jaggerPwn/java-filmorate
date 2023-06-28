package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FeedDBStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getFeed(long id) {
        String sql = "SELECT * " +
                "FROM feed " +
                "WHERE userid = ?; ";
        log.debug("Получен список Event для User c ID {}.", id);
        return jdbcTemplate.query(sql, this::mapToEvent, id);
    }

    @Override
    public Event saveFeed(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("feed")
                .usingGeneratedKeyColumns("id");

        Number key = simpleJdbcInsert.executeAndReturnKey(eventToMap(event));
        event.setEventId((long) key);
        log.debug("Event c ID {} создан.", event.getEventId());
        return event;
    }

    private Event mapToEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("id"))
                .userId(rs.getLong("userId"))
                .timestamp(rs.getLong("timestamp"))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entityId"))
                .build();
    }

    public Map<String, Object> eventToMap(Event event) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("userId", event.getUserId());
        temp.put("timestamp", event.getTimestamp());
        temp.put("eventType", event.getEventType().name());
        temp.put("operation", event.getOperation().name());
        temp.put("entityId", event.getEntityId());
        return temp;
    }
}
