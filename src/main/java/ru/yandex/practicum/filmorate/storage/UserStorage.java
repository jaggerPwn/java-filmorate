package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User put(User user);

    Map<Integer, User> getUsers();

    User getUserById(int id);
}
