package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend (int user1id, int user2id) {
        User user1 = userStorage.getUser(user1id);
        User user2 = userStorage.getUser(user2id);
        user1.addToFriendsId(user2id);
        user2.addToFriendsId(user1id);
        userStorage.updateUser(user1);
        userStorage.updateUser(user2);

    }

    public void deleteFriend (int user1id, int user2id) {
        User user1 = userStorage.getUser(user1id);
        User user2 = userStorage.getUser(user2id);
        user1.removeFromFriendsId(user2id);
        user2.removeFromFriendsId(user1id);
        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
        }

    public List<User> getCommonFriends (int user1id, int user2id) {
        User user1 = userStorage.getUser(user1id);
        User user2 = userStorage.getUser(user2id);
        List <User> commonFriends = new ArrayList<>();
        List <Integer> commonID =  user1.getFriedndsId().stream()
                .filter(user2.getFriedndsId()::contains)
                .distinct()
                .collect(Collectors.toList());
        for (int id : commonID) {
            commonFriends.add(userStorage.getUser(id));
        }
            return commonFriends;
    }

    public List <User> getFriendsOfId(int id) {
        User user = userStorage.getUser(id);
        List <User> friendsOfId = new ArrayList<>();
        if (user != null && !user.getFriedndsId().isEmpty()) {
            for (int friendId : user.getFriedndsId()) {
                userStorage.getAllUsers().stream()
                        .filter(userById -> friendId == userById.getId())
                        .forEachOrdered(friendsOfId::add);
            }
        }
        return friendsOfId;
    }

}
