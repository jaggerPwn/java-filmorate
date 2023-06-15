package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

public interface UserService {
    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Collection<User> getUserFriends(int userId);

    Set<User> getCommonFriends(int userId, int friendId);

    Collection<User> deleteAll();

    UserStorage getUserStorage();
}
