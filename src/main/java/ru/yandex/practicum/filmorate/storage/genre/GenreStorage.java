package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    public List<Genre> getAllGenres();

    public Genre getGenre(int id);

    public Set<Genre> getFilmGenres(int id);

    public void addGenre(Film film);

    public void removeGenres(Film film);
}
