package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;


@Data
public class User {
    @PositiveOrZero
    private int id;
    @Email
    private String email;
    @NotNull
    private String login;
    private String name;
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthday;

    Set<Integer> friedndsId = new HashSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if ((name == null)||name.isBlank()) this.name = login;
        else this.name = name;
        this.birthday = birthday;
    }

    public void addToFriendsId (Integer id) {
        friedndsId.add(id);
    }
    public void removeFromFriendsId (Integer id) {
        friedndsId.remove(id);
    }
}
