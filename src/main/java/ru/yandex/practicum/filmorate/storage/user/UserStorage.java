package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;


import java.util.List;
import java.util.Optional;

public interface UserStorage {

    public List<User> getAllUsers();

    public Optional<User> getUser(int id);

    public User updateUser(User user);

    public User register(User user);

}
