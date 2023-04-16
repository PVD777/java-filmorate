package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
public class User {
    @PositiveOrZero
    private int id;
    @Email
    @NotEmpty
    private String email;
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9]+")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private Set<Integer> friedndsId = new HashSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if ((name == null) || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }

    public String getName() {
        if ((name == null) || name.isBlank()) {
            return login;
        }
        return name;
    }

    public void addToFriendsId(Integer id) {
        friedndsId.add(id);
    }

    public void removeFromFriendsId(Integer id) {
        friedndsId.remove(id);
    }
}
