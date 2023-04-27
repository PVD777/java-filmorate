package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private MpaStorage mpaStorage;
    private LikesStorage likesStorage;
    private GenreStorage genreStorage;

    private final RowMapper<Film> filmRowMapper = (resultSet, rowNum) -> {
        Film film = new Film(
                resultSet.getString("name"),
                resultSet.getString("description"),
                LocalDate.parse(resultSet.getString("release")),
                resultSet.getInt("duration")
        );
        film.setMpa(mpaStorage.getMpa(resultSet.getInt("mpa_id")).get());
        film.setId(resultSet.getInt("film_id"));
        film.setLikesCounter(likesStorage.getCountOfLike(film.getId()));
        film.setIdOfLikers(likesStorage.getIdOfLikers(film.getId()));
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
        return film;
    };

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS", filmRowMapper);
    }

    @Override
    public Film addFilm(Film film) {
        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingColumns("name", "description", "release", "duration", "mpa_id")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKeyHolder(Map.of("name", film.getName(),
                        "description", film.getDescription(),
                        "release", film.getReleaseDate(),
                        "duration", film.getDuration(),
                        "mpa_id", film.getMpa().getId()))
                .getKeys();
        film.setId((Integer) keys.get("film_id"));
        genreStorage.addGenre(film);
        film.setMpa(mpaStorage.getMpa(film.getMpa().getId()).get());
        film.setLikesCounter(likesStorage.getCountOfLike(film.getId()));
        film.setIdOfLikers(likesStorage.getIdOfLikers(film.getId()));
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
        log.info("Добавлен новый фильм: id={}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!isFilmExists(film.getId())) {
            log.info("Фильм с идентификатором {} отсутствует.", film.getId());
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        String sql = "UPDATE FILMS SET name = ?, description = ?, release = ?, duration = ?, mpa_id = ?" +
                " WHERE film_id = " + film.getId();
        jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        genreStorage.removeGenres(film);
        genreStorage.addGenre(film);
        film.setMpa(mpaStorage.getMpa(film.getMpa().getId()).get());
        film.setLikesCounter(likesStorage.getCountOfLike(film.getId()));
        film.setIdOfLikers(likesStorage.getIdOfLikers(film.getId()));
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
        log.info("Фильм с идентификатором {} обновлен.", film.getId());

        return film;
    }

    @Override
    public Optional<Film> getFilm(int id) {
        if (!isFilmExists(id)) {
            log.info("Фильм с идентификатором {} отсутствует.", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        Film film = jdbcTemplate.queryForObject("SELECT * FROM FILMS WHERE FILM_ID = ?",
                filmRowMapper, id);
        film.setMpa(mpaStorage.getMpa(film.getMpa().getId()).get());
        film.setLikesCounter(likesStorage.getCountOfLike(film.getId()));
        film.setIdOfLikers(likesStorage.getIdOfLikers(film.getId()));
        log.info("Найден фильм: {} {}", film.getId(), film.getName());
        return Optional.of(film);

    }

    @Override
    public List<Film> getSearchingFilms(String query, String[] by) {
        List<String> keyTags;
        if (by == null) {
            String sql = "SELECT * FROM FILMS AS F " +
                    "LEFT JOIN (SELECT film_id, COUNT(user_id) AS RATE FROM LIKES GROUP BY film_id) AS R " +
                    "ON F.film_id = R.film_id " +
                    "ORDER BY R.RATE DESC";
            return jdbcTemplate.query(sql, filmRowMapper);
        } else {
            keyTags = List.of(by);
        }
        query = "%" + query + "%";
        if (keyTags.contains("director") && keyTags.contains("title")) {
            String sql = "SELECT * FROM FILMS AS F " +
                    "LEFT JOIN (SELECT film_id, COUNT(user_id) AS RATE FROM LIKES GROUP BY film_id) AS R " +
                    "ON F.film_id = R.film_id " +
                    "LEFT JOIN FILM_DIRECTOR AS FD ON F.film_id = FD.film_id " +
                    "LEFT JOIN DIRECTOR AS D ON FD.director_id = D.id " +
                    "WHERE D.name ILIKE ? OR F.name ILIKE ? ORDER BY R.RATE DESC";
            return jdbcTemplate.query(sql, filmRowMapper, query, query);
        } else if (keyTags.contains("director")) {
            String sql = "SELECT * FROM FILMS AS F " +
                    "LEFT JOIN (SELECT film_id, COUNT(user_id) AS RATE FROM LIKES GROUP BY film_id) AS R " +
                    "ON F.film_id = R.film_id " +
                    "LEFT JOIN FILM_DIRECTOR AS FD ON F.film_id = FD.film_id " +
                    "LEFT JOIN DIRECTOR AS D ON FD.director_id = D.id " +
                    "WHERE D.name ILIKE ? ORDER BY R.RATE DESC";
            return jdbcTemplate.query(sql, filmRowMapper, query);
        } else if (keyTags.contains("title")) {
            String sql = "SELECT * FROM FILMS AS F " +
                    "LEFT JOIN (SELECT film_id, COUNT(user_id) AS RATE FROM LIKES GROUP BY film_id) AS R " +
                    "ON F.film_id = R.film_id " +
                    "WHERE F.name ILIKE ? ORDER BY R.RATE DESC";
            return jdbcTemplate.query(sql, filmRowMapper, query);
        } else {
            String sql = "SELECT * FROM FILMS AS F " +
                    "LEFT JOIN (SELECT film_id, COUNT(user_id) AS RATE FROM LIKES GROUP BY film_id) AS R " +
                    "ON F.film_id = R.film_id " +
                    "ORDER BY R.RATE DESC";
            return jdbcTemplate.query(sql, filmRowMapper);
        }
    }


    private boolean isFilmExists(int id) {
        String sql = "SELECT count(*) FROM FILMS WHERE film_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        return count > 0;
    }
}
