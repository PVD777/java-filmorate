package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/films")

public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping()
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @PostMapping()
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLikeToFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.putLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFromFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLikeFromFilm(id, userId);
    }
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count"
            ,required = false, defaultValue = "10") @Positive int count) {
        return filmService.getPopularFilm(count);
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable int id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new FilmNotFoundException("Запрошенный фильм не существует");
        } else {
            return film;
        }
    }
}
