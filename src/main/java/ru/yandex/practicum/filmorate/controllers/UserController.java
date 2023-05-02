package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping()
    public User register(@RequestBody @Valid User user) {
        return userService.register(user);
    }

    @PutMapping()
    public User updateUser(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFrind(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommenFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendOfId(@PathVariable int id) {
        return userService.getFriendsOfId(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendateFilm(@PathVariable int id) {
        return userService.getRecommendateFilm(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUsersFeed(@PathVariable Integer id) {
        return userService.getEventByUserId(id);
    }
}

