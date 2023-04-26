package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Validated()
public class FilmController {

    private final FilmService filmService;

    @GetMapping()
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping()
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLikeToFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.putLikeToFilm(id, userId);
    }

    @DeleteMapping("/{filmId}")
    public Film deleteLikeFromFilm(@PathVariable int filmId) {
        return filmService.deleteFilm(filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFromFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") @Positive int count) {
        return filmService.getPopularFilm(count);
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable int id) {
            return filmService.getFilm(id);
    }
}
