package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    public static final LocalDate FIRST_FILM = LocalDate.of(1895, Month.DECEMBER, 28);

    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;


    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(++idCounter);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм {}", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Выполнено обновление фильма {}", film.getName());
            return film;
        } else {
            throw new FilmNotFoundException("Фильм с таким id не существует");
        }
    }

    @Override
    public Optional<Film> getFilm(int id) {
        return Optional.ofNullable(films.get(id));
    }

}
