package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Set;

public interface LikesStorage {

    void setLikeToFilm(int userId, int filmId);

    void deleteLikeFromFilm(int userId, int filmId);

    int getCountOfLike(int filmId);

    Set<Integer> getIdOfLikers(int filmId);

}
