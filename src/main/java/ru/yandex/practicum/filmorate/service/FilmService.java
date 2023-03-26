package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }
    public Film putLikeToFilm(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (film.getIdOfLikers().isEmpty() || !film.getIdOfLikers().contains(userId)) {
            film.setLikesCounter(film.getLikesCounter()+1);
            film.addIdOfLikers(userId);
        }
        filmStorage.updateFilm(film);
        return film;
    }

    public Film deleteLikeFromFilm(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (film.getIdOfLikers().contains(userId)) {
            film.setLikesCounter(film.getLikesCounter()-1);
            film.deleteIdOfLikers(userId);
        }
        filmStorage.updateFilm(film);
        return film;
    }
    public List<Film> getPopularFilm(int count) {
        List <Film> sortedFilmList = filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(Film :: getLikesCounter)
                .reversed())
                .limit(count)
                .collect(Collectors.toList());
        return sortedFilmList;
    }
}
