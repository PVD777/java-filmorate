package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAll();

    Director getById(Integer id);

    List<Director> getByFilm(Integer idFilm);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer id);

    void checkDirector(Integer id);
}
