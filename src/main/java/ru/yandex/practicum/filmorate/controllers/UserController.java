package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 0;
    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping()
    public User register(@RequestBody @Valid User user) {
        isSpaceInLogin(user);
        user.setId(++idGenerator);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь {}", user.getName());
        return user;
    }

    @PutMapping()
    public User updateUser (@RequestBody @Valid User user) {
        if (users.containsKey(user.getId())) {
            isSpaceInLogin(user);
            users.put(user.getId(), user);
            log.info("Выполнено обновление пользовтеля {}", user.getName());
            return user;
        }
        else
            log.error("Попытка обновления пользователя с несуществующим id {}", user.getId());
        throw new ValidationException("Пользователь с таким id не существует");
    }

    public void isSpaceInLogin (User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("В логине присутствуют пробелы");
            throw new ValidationException("В логине присутствуют пробелы");
        }
    }
}
