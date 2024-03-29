package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Genre> genreRowMapper = (resultSet, rowNum) -> {
        Genre genre = new Genre(
                resultSet.getInt("genre_id"),
                resultSet.getString("name")
        );
        return genre;
    };


    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE", genreRowMapper);
    }

    @Override
    public Genre getGenre(int id) {
        String sql = "SELECT count(*) FROM GENRE WHERE genre_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
        if (count < 1) {
            throw new GenreNotFoundException("Жанр с id " + id + " не найден");
        }
            Genre genre = jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE genre_id = ?", genreRowMapper, id);
            return genre;
    }

    @Override
    public Set<Genre> getFilmGenres(int id) {

       /* String sql = "SELECT genre.genre_id, name FROM FILM_GENRE" +
                "" +
                "JOIN genre ON film_genre.genre_id = genre.genre_id\n" +
                "WHERE film_id = ?\n" +
                "ORDER BY genre.genre_id";*/
        String sql = "SELECT genre_id FROM FILM_GENRE WHERE film_id = ?";
        Set<Genre> genres = new HashSet<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (rowSet.next()) {
            genres.add(getGenre(rowSet.getInt("genre_id")));
        }
        return genres;
    }

    @Override
    public void addGenre(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void removeGenres(Film film) {
        jdbcTemplate.update("DELETE FROM FILM_Genre WHERE film_id = ?", film.getId());
    }
}
