package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.getUserStorage().findAll();
    }

    @GetMapping("/{id}/friends")
    public Set<User> getUserFriends(@PathVariable("id") int userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable("userId") int userId) {
        return userService.getUserStorage().getUserById(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.getUserStorage().create(user);
    }

    @PutMapping
    public User put(@RequestBody User user) {
        return userService.getUserStorage().put(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public Map<String, String> addFriend(@PathVariable("userId") int userId, @PathVariable("friendId") int friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public Map<String, String> deleteFriend(@PathVariable("userId") int userId, @PathVariable("friendId") int friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @DeleteMapping
    public Map<Integer, User> deleteAll() {
        return userService.deleteAll();
    }
}
