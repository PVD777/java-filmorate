package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class MpaDBStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Mpa> mpaRowMapper = (resultSet, rowNum) -> new Mpa(
            resultSet.getInt("mpa_id"),
            resultSet.getString("mpaName")
    );

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA_RATING", mpaRowMapper);
    }

    @Override
    public Optional<Mpa> getMpa(int id) {
        String sql = "SELECT count(*) FROM MPA_RATING WHERE mpa_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
        if (count < 1) {
            throw new MpaNotFoundException("MPA с id " + id + " не найден");
        }
        return Optional.of(jdbcTemplate.queryForObject("SELECT * FROM MPA_RATING WHERE mpa_id = ?", mpaRowMapper, id));
    }

}
