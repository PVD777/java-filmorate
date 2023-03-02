package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private List<User> users = new ArrayList<>();
    private int idGenerator = 1;
    @GetMapping()
    public List<User> findAll() {
        return users;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        user.setId(idGenerator++);
        users.add(user);
        return user;
    }
}
