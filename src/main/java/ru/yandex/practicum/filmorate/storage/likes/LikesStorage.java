package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Set;

public interface LikesStorage {

    public void setLikeToFilm(int userId, int filmId);

    public void deleteLikeFromFilm(int userId, int filmId);

    public int getCountOfLike(int filmId);

    public Set<Integer> getIdOfLikers(int filmId);

}
