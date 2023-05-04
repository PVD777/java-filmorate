package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;


import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getAllUsers();

    Optional<User> getUser(int id);

    User updateUser(User user);

    User register(User user);

    void removeUser(int userId);

}
