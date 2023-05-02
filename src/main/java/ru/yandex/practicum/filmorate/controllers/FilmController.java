package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.SortingFilm;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") @Positive int count,
                                      @RequestParam(value = "genreId", defaultValue = "0") @PositiveOrZero int genreId,
                                      @RequestParam(value = "year", defaultValue = "0") @PositiveOrZero int year) {
        return filmService.getPopularFilm(count, genreId, year);
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/search")
    public List<Film> getSearchingFilms(@RequestParam(value = "query", required = false) String query,
                                        @RequestParam(value = "by", required = false) String[] by) {
        return filmService.getSearchingFilms(query, by);
    }

    @GetMapping(value = "/director/{directorId}")
    public List<Film> getFilmsByDirectorId(@PathVariable Integer directorId, @RequestParam SortingFilm sortBy) {
        return filmService.getFilmsByDirectorId(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId") @Positive int userId,
                                     @RequestParam(value = "friendId") @Positive int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
