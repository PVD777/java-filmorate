package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventTypes;
import ru.yandex.practicum.filmorate.model.OperationTypes;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getByUserId(Integer id) {
        checkUser(id);
        String sql = "select * from event where user_id = ?";
        return jdbcTemplate.query(sql, eventRowMapper(), id);
    }

    @Override
    public void addEvent(Event event) {
        int id = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENT")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(Map.of(
                        "creation_date", event.getTimestamp(),
                        "user_id", event.getUserId(),
                        "event_type", event.getEventType(),
                        "operation", event.getOperation(),
                        "entity_id", event.getEventId()))
                .intValue();
        log.info("Создано событие c id {} пользователя c id {}.", id, event.getUserId());
    }

    private RowMapper<Event> eventRowMapper() {
        return (rs, rowNum) -> new Event(
                rs.getInt("id"),
                rs.getLong("creation_date"),
                rs.getInt("user_id"),
                EventTypes.valueOf(rs.getString("event_type")),
                OperationTypes.valueOf(rs.getString("operation")),
                rs.getInt("entity_id")
        );
    }

    private void checkUser(int id) {
        String sql = "SELECT count(*) FROM USERS WHERE user_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);
        if (!row.next()) {
            log.warn("Пользователь с идентификатором {} отсутствует.", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
    }
}
