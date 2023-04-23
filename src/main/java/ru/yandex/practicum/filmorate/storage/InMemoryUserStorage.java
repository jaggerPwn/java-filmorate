package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.exception.ValidationException500;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class InMemoryUserStorage implements UserStorage {
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

        if (user.getName() == null) {
            user.setName(user.getLogin());
        } else if (user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }


    @Override
    public User put(User user) {
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
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User getUserById(int id) {
        User user = null;
        for (User value : users.values()) {
            if (value.getId() == id) user = value;
        }

        if (user == null) throw new ValidationException404("No such user id: " + id);
        return user;
    }
}
