package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortingFilm;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    @Qualifier(value = "filmDbStorage")
    private final FilmStorage filmStorage;

    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final LikesStorage likesStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id).orElseThrow(() -> new FilmNotFoundException("Фильм не найден"));
    }

    public Film putLikeToFilm(int id, int userId) {
        Film film = getFilm(id);
        User user = userService.getUser(userId);
        likesStorage.setLikeToFilm(userId, id);
        return film;
    }

    public Film deleteLikeFromFilm(int id, int userId) {
        Film film = getFilm(id);
        User user = userService.getUser(userId);
        likesStorage.deleteLikeFromFilm(userId, id);
        return film;
    }

    public List<Film> getPopularFilm(int count) {
        List<Film> sortedFilmList = filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesCounter)
                .reversed())
                .limit(count)
                .collect(Collectors.toList());
        return sortedFilmList;
    }

    public List<Film> getFilmsByDirectorId(Integer directorId, SortingFilm sortBy) {
        return filmStorage.getFilmsByDirectorId(directorId, sortBy);
    }
}
