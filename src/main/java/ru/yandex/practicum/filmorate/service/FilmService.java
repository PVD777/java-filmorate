package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }


    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    };

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id).orElseThrow(() ->new FilmNotFoundException("Фильм не найден"));
    }

    public Film putLikeToFilm(int id, int userId) {
        Film film = getFilm(id);
        User user = userService.getUser(userId);
        if (film.getIdOfLikers().isEmpty() || !film.getIdOfLikers().contains(userId)) {
            film.setLikesCounter(film.getLikesCounter() + 1);
            film.addIdOfLikers(userId);
        }
        return film;
    }

    public Film deleteLikeFromFilm(int id, int userId) {
        Film film = getFilm(id);
        User user = userService.getUser(userId);
        if (film.getIdOfLikers().contains(userId)) {
            film.setLikesCounter(film.getLikesCounter() - 1);
            film.deleteIdOfLikers(userId);
        }
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
}
