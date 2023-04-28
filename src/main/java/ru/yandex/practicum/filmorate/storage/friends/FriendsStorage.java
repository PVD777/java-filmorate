package ru.yandex.practicum.filmorate.storage.friends;



public interface FriendsStorage {

    void addToFriend(int user1id, int user2id);

    void removeFromFriend(int user1id, int user2id);

}