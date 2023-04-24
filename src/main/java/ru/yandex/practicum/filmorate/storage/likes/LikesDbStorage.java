package ru.yandex.practicum.filmorate.storage.likes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void setLikeToFilm(int userId, int filmId) {
        String sql = "Select * FROM LIKES WHERE user_id = ? AND film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, filmId);
        if (rowSet.next()) {
            log.info("От id = {} на фильм Id = {} лайк уже есть", userId, filmId);
            return;
        }
        new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("LIKES")
                .usingColumns("user_id", "film_id")
                .execute(Map.of("user_id", userId,
                        "film_id", filmId));
    }

    @Override
    public void deleteLikeFromFilm(int userId, int filmId) {
        String sql = "Select * FROM LIKES WHERE user_id = ? AND film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, filmId);
        if (rowSet.next()) {
            jdbcTemplate.update("DELETE FROM LIKES WHERE user_id = ? AND film_id = ?", userId, filmId);
            log.info("От id = {} на фильм Id = {} лайк удален", userId, filmId);
            return;
        }
        log.info("От id = {} на фильм Id = {} лайка не было", userId, filmId);
    }

    @Override
    public int getCountOfLike(int filmId) {
        String sql = "SELECT count(*) FROM LIKES WHERE film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmId);
        rowSet.next();
        return rowSet.getInt("count(*)");
    }

    @Override
    public Set<Integer> getIdOfLikers(int filmId) {
        String sql = "SELECT user_id FROM LIKES WHERE film_id = ?";
        Set<Integer> likersId = new HashSet<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmId);
        while (rowSet.next()) {
            likersId.add(rowSet.getInt("user_id"));
        }
        return likersId;
    }

}
