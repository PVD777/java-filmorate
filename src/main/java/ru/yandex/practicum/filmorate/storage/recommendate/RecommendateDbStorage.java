package ru.yandex.practicum.filmorate.storage.recommendate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class RecommendateDbStorage implements  RecommendateFilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    public List<Optional<Film>> getRecommendate(int id) {
        String sql = "SELECT film_id  FROM LIKES l WHERE user_id IN (SELECT user_id  FROM LIKES l2 WHERE user_id != ? " +
                "AND film_id in (SELECT film_id FROM LIKES l WHERE user_id =?)  GROUP BY user_id ) " +
                "AND film_id NOT IN (SELECT film_id FROM LIKES l WHERE user_id =?) GROUP BY FILM_ID ;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int id1 = rs.getInt("film_id");
            return filmDbStorage.getFilm(id1);
        }, id, id, id);
    }
}
