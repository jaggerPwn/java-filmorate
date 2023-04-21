package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException500;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
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
}
