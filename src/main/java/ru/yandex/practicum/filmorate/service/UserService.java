package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.recommendate.RecommendateFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class UserService {

    @Qualifier(value = "userDbStorage")
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private  final RecommendateFilmStorage recommendateFilmStorage;

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
        friendsStorage.addToFriend(user1id, user2id);
    }

    public void deleteFriend(int user1id, int user2id) {
        friendsStorage.removeFromFriend(user1id, user2id);
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

    public List<Film> getRecommendateFilm(int userId) {
        List<Film> recommendateFilm = new ArrayList<>();
        List<Optional<Film>> films = recommendateFilmStorage.getRecommendate(userId);
        if (!films.isEmpty()) {
            for (Optional<Film> film : films) {
                film.ifPresent(recommendateFilm::add);
            }
        }
        return recommendateFilm;
    }

}
