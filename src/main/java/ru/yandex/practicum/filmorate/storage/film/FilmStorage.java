package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortingFilm;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilm(int id);

    List<Film> getSearchingFilms(String query, String[] by);

    List<Film> getFilmsByDirectorId(Integer directorId, SortingFilm sortBy);
    List<Film> getCommonFilms(int userId, int friendId);
}
