package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    void setId(int id);

    User put(User user);

    Map<Integer, User> getUsers();

    User getUserById(int id);
}
