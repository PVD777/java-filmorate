package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {


    @GetMapping()
    public List<Film> getAllFilms();

    @PostMapping()
    public Film addFilm(@RequestBody @Valid Film film);

    @PutMapping()
    public Film updateFilm(@RequestBody @Valid Film film);

    @GetMapping()
    public Film getFilm(int id);
}
