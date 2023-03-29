package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    public List<Film> getAllFilms();

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Optional<Film> getFilm(int id);
}
