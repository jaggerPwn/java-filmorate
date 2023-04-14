package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException500;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
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

    @PutMapping
    public User put(@RequestBody User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException400("Дата рождения не может быть в будущем");
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException500("No such user id: " + user.getId());
        }
        users.put(user.getId(), user);

        return user;
    }
}
