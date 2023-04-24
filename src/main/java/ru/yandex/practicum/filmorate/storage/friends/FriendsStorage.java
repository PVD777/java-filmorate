package ru.yandex.practicum.filmorate.storage.friends;



public interface FriendsStorage {

    public void addToFriend(int user1id, int user2id);

    public void removeFromFriend(int user1id, int user2id);

}