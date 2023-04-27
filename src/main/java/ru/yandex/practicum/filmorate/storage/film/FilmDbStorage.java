package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.SortingFilm;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
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
                .usingColumns("name", "description", "release", "duration","mpa_id")
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
        return  film;
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
                film.getName(),film.getDescription(),film.getReleaseDate(),film.getDuration(), film.getMpa().getId());
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
        log.info("Найден фильм: {} {}", film.getId(),film.getName());
        return Optional.of(film);

    }

    public List<Film> getFilmsByDirectorId(Integer directorId, SortingFilm sortBy) {
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
}
