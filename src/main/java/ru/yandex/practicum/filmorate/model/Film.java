package ru.yandex.practicum.filmorate.model;

import lombok.Data;


import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import javax.validation.constraints.*;

@Data
public class Film {


    private int id;
    @NotBlank (message = "Название фильма не может быть пустым")
    private String name;
    @NotBlank (message = "Описание фильма не может быть пустым")
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @NotNull (message = "Длительность фильма должна быть положительной")
    @Positive (message = "Длительность фильма должна быть положительной")
    private int duration;

    public Film(String name, String description, String releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.duration = duration;
    }
}
