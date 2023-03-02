package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FilmController {

    private List<Film> posts = new ArrayList<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        return posts;
    }

    @PostMapping(value = "/film")
    public Film create(@RequestBody Film film) {
        posts.add(film);
        return film;
    }
}
