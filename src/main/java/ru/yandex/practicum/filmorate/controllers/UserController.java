package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;
    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @PostMapping()
    public User register(@RequestBody @Valid User user) {
        return userStorage.register(user);
    }

    @PutMapping()
    public User updateUser (@RequestBody @Valid User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend (@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFrind (@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List <User> getCommenFriends (@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("{id}")
    public User getUser (@PathVariable int id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new UserNotFoundException("Запрошенный пользователь не существует");
        } else {
            return user;
        }
    }

    @GetMapping("/{id}/friends")
    public List <User> getFriendOfId (@PathVariable int id) {
        return userService.getFriendsOfId(id);
    }

}

