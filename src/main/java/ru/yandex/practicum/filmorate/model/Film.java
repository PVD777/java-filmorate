package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import javax.validation.constraints.*;

@Data
public class Film {
    private int id;
    @NotBlank (message = "Название фильма не может быть пустым")
    private String name;
    @NotBlank (message = "Описание фильма не может быть пустым")
    @Size(max = 200)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate releaseDate;
    @NotNull (message = "Длительность фильма должна быть положительной")
    @Positive (message = "Длительность фильма должна быть положительной")
    private int duration;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate=releaseDate;
        this.duration = duration;
    }

}
