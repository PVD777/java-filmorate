package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenre(int id);

    Set<Genre> getFilmGenres(int id);

    void addGenre(Film film);

    void removeGenres(Film film);
}
