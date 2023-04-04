package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.IsAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank (message = "Название фильма не может быть пустым")
    private String name;
    @NotBlank (message = "Описание фильма не может быть пустым")
    @Size(max = 200)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @IsAfter(current = "1895-12-28")
    private LocalDate releaseDate;
    @Positive (message = "Длительность фильма должна быть положительной")
    private int duration;
    private int likesCounter;
    private Set<Integer> idOfLikers;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        idOfLikers = new HashSet<>();
    }

    public void addIdOfLikers(int id) {
        idOfLikers.add(id);
    }

    public void deleteIdOfLikers(int id) {
        idOfLikers.remove(id);
    }

}
