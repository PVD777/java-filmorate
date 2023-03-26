package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {

    @GetMapping()
    public List<User> getAllUsers();

    @GetMapping()
    public User getUser(int id);

    @PutMapping()
    public User updateUser (@RequestBody @Valid User user);

    @PostMapping()
    public User register(@RequestBody @Valid User user);

}
