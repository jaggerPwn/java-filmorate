package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface UserService {
    Map<String, String> addFriend(int userId, int friendId);

    Map<String, String> deleteFriend(int userId, int friendId);

    UserStorage getUserStorage();

    Set<User> getUserFriends(int userId);

    Set<User> getCommonFriends(int userId, int friendId);

    Collection<User> deleteAll();
}
