package ru.yandex.practicum.filmorate.storage.inmemory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.exception.ValidationException500;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements Storage<User> {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException400("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public User update(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException500("Дата рождения не может быть в будущем");
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException500("No such user id: " + user.getId());
        }
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User getById(int id) {
        User user = null;
        for (User value : users.values()) {
            if (value.getId() == id) user = value;
        }

        if (user == null) throw new ValidationException404("No such user id: " + id);
        return user;
    }

    @Override
    public void clear() {

    }
}