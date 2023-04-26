package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Director> getAll() {
        String sql = "select * from director";
        return jdbcTemplate.query(sql, directorRowMapper());
    }

    @Override
    public Director getById(Integer id) {
        String sql = "select * from director where director_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, directorRowMapper(), id);
        } catch (DataAccessException e) {
            log.warn("Режиссёр с id={} не найден.", id);
            throw new DirectorNotFoundException("Режиссёр с id " + id + " не найден.");
        }
    }

    @Override
    public List<Director> getByFilm(Integer idFilm) {
        String sql = "select d.director_id, d.name " +
                "from director as d " +
                "left join film_director as fd on d.director_id = fd.director_id " +
                "where film_id = ?";
        return null; //TODO не факт что пригодиться.

    }

    @Override
    public Director addDirector(Director director) {
        int directorId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id")
                .executeAndReturnKey(Map.of("name", director.getName()))
                .intValue();

        return getById(directorId);
    }

    @Override
    public Director updateDirector(Director director) {
        Integer id = director.getId();
        String sql = "update director set name = ? where director_id = ?";

        if (isExists(id)) {
            jdbcTemplate.update(sql, director.getName());

            return getById(id);
        } else {
            log.warn("Режиссёр с id={} не найден.", id);
            throw new DirectorNotFoundException("Режиссёр с id " + id + " не найден.");
        }

    }

    @Override
    public void deleteDirector(Integer id) {
        if (isExists(id)) {
            String sql = "delete from director where director_id = ?";
            jdbcTemplate.update(sql, id);
        } else {
            log.warn("Режиссёр с id={} не найден.", id);
            throw new DirectorNotFoundException("Режиссёр с id " + id + " не найден.");
        }
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(
                rs.getInt("director_id"),
                rs.getString("name")
        );
    }

    private boolean isExists(Integer id) {
        String sql = "select * from director where director_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql);

        return row.next();
    }
}
