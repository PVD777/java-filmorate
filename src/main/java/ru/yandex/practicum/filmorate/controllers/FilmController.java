package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;

    @GetMapping()
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping()
    public Film addFilm (@RequestBody @Valid Film film) {
        isUpToDateFilm(film);
        film.setId(++idCounter);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм {}", film.getName());
        return film;
    }

    @PutMapping()
    public Film updateFilm (@RequestBody @Valid Film film) {
        if (films.containsKey(film.getId())) {
            isUpToDateFilm(film);
            films.put(film.getId(), film);
            log.info("Выполнено обновление фильма {}", film.getName());
            return film;
        }
        else {
            log.error("Попытка обновления фильма с несуществующим id {}", film.getId());
            throw new ValidationException("Фильм с таким id не существует");
        }

    }

    public void isUpToDateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Попытка добавления фильма с несуществующей датой");
            throw new ValidationException("Необходимо проверить дату фильма");
        }
    }
}
