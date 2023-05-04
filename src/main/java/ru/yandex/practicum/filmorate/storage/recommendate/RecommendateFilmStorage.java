package ru.yandex.practicum.filmorate.storage.recommendate;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface RecommendateFilmStorage {

    List<Optional<Film>> getRecommendate(int id);

}
