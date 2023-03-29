package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public List<User> getAllUsers() {
        return  userStorage.getAllUsers();
    }

    public User getUser(int id) {
        return userStorage.getUser(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User register(User user) {
        return userStorage.register(user);
    }

    public void addFriend(int user1id, int user2id) {
        User user1 = getUser(user1id);
        User user2 = getUser(user2id);
        user1.addToFriendsId(user2id);
        user2.addToFriendsId(user1id);
    }

    public void deleteFriend(int user1id, int user2id) {
        User user1 = getUser(user1id);
        User user2 = getUser(user2id);
        user1.removeFromFriendsId(user2id);
        user2.removeFromFriendsId(user1id);
    }

    public List<User> getCommonFriends(int user1id, int user2id) {
        User user1 = getUser(user1id);
        User user2 = getUser(user2id);
        return user1.getFriedndsId().stream()
                .filter(user2.getFriedndsId()::contains)
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getFriendsOfId(int id) {
        User user = getUser(id);
        return user.getFriedndsId().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

}
