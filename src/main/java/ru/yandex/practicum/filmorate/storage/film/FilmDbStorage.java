package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.SortingFilm;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String ADD_FILM_DIRECTOR = "insert into film_director (film_id, director_id) values (?, ?)";
    private static final String DELETE_FILM_DIRECTOR = "delete from film_director where film_id = ?";
    private final JdbcTemplate jdbcTemplate;
    private MpaStorage mpaStorage;
    private LikesStorage likesStorage;
    private GenreStorage genreStorage;
    private DirectorStorage directorStorage;

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
        film.setDirectors(directorStorage.getByFilm(film.getId()));
        return film;
    };

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS", filmRowMapper);
    }

    @Override
    public Film addFilm(Film film) {
        List<Director> directors = new ArrayList<>(film.getDirectors());
        int filmId = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingColumns("name", "description", "release", "duration","mpa_id")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKey(Map.of("name", film.getName(),
                        "description", film.getDescription(),
                        "release", film.getReleaseDate(),
                        "duration", film.getDuration(),
                        "mpa_id", film.getMpa().getId()))
                .intValue();
        film.setId(filmId);
        genreStorage.addGenre(film);

        if (!directors.isEmpty()) {
            jdbcTemplate.batchUpdate(ADD_FILM_DIRECTOR, initFilmDirectorValues(filmId, directors));
        }

        film.setMpa(mpaStorage.getMpa(film.getMpa().getId()).get());
        film.setLikesCounter(likesStorage.getCountOfLike(filmId));
        film.setIdOfLikers(likesStorage.getIdOfLikers(filmId));
        film.setGenres(genreStorage.getFilmGenres(filmId));
        film.setDirectors(directorStorage.getByFilm(filmId));
        log.info("Добавлен новый фильм: id={}", filmId);

        return  film;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        List<Director> directors = new ArrayList<>(film.getDirectors());

        if (!isFilmExists(film.getId())) {
            log.info("Фильм с идентификатором {} отсутствует.", filmId);
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
        }

        String sql = "UPDATE FILMS SET name = ?, description = ?, release = ?, duration = ?, mpa_id = ?" +
                " WHERE film_id = " + filmId;
        jdbcTemplate.update(sql,
                film.getName(),film.getDescription(),film.getReleaseDate(),film.getDuration(), film.getMpa().getId());
        genreStorage.removeGenres(film);
        genreStorage.addGenre(film);

        jdbcTemplate.update(DELETE_FILM_DIRECTOR, filmId);
        if (!directors.isEmpty()) {
            jdbcTemplate.batchUpdate(ADD_FILM_DIRECTOR, initFilmDirectorValues(filmId, directors));
        }

        film.setMpa(mpaStorage.getMpa(film.getMpa().getId()).get());
        film.setLikesCounter(likesStorage.getCountOfLike(filmId));
        film.setIdOfLikers(likesStorage.getIdOfLikers(filmId));
        film.setGenres(genreStorage.getFilmGenres(filmId));
        film.setDirectors(directorStorage.getByFilm(filmId));
        log.info("Фильм с идентификатором {} обновлен.", filmId);

        return film;
    }

    @Override
    public Optional<Film> getFilm(int id) {
        if (!isFilmExists(id)) {
            log.info("Фильм с идентификатором {} отсутствует.", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }

        String sql = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);

        film.setMpa(mpaStorage.getMpa(film.getMpa().getId()).get());
        film.setLikesCounter(likesStorage.getCountOfLike(id));
        film.setIdOfLikers(likesStorage.getIdOfLikers(id));
        film.setDirectors(directorStorage.getByFilm(id));
        log.info("Найден фильм: {} {}", id, film.getName());

        return Optional.of(film);
    }

    @Override
    public List<Film> getFilmsByDirectorId(Integer directorId, SortingFilm sortBy) {
        directorStorage.checkDirector(directorId);
        String sql = "select f.film_id, f.name, f.description, f.duration, f.release, f.mpa_id, mr.mpaName " +
                "from films as f " +
                "left join mpa_rating as mr on f.mpa_id = mr.mpa_id " +
                "left join film_director as fd on f.film_id = fd.film_id ";
        switch (sortBy) {
            case year:
                sql += "where fd.director_id = ? order by f.release";
                break;
            case likes:
                sql += "left join likes as l on f.film_id = l.film_id " +
                        "where fd.director_id = ? group by f.film_id order by count(l.film_id) desc";
                break;
        }
        return jdbcTemplate.query(sql, filmWithDirectorRowMapper(), directorId);
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
        int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
        return count > 0;
    }

    private RowMapper<Film> filmWithDirectorRowMapper() {
        return ((rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release").toLocalDate(),
                rs.getInt("duration"),
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpaName")),
                genreStorage.getFilmGenres(rs.getInt("film_id")),
                directorStorage.getByFilm(rs.getInt("film_id"))
        ));
    }

    private BatchPreparedStatementSetter initFilmDirectorValues(Integer filmId, List<Director> directors) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, filmId.toString());
                ps.setString(2, directors.get(i).getId().toString());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        };
    }
}
