package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.EventDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FeedService feedService;
    private final RecommendationService recommendationService;

    @Autowired
    public UserController(UserService userService, FeedService feedService, RecommendationService recommendationService) {
        this.userService = userService;
        this.feedService = feedService;
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO newUser) {
        log.info("Получен POST запрос по эндпоинту '/users' на создание user");
        return new ResponseEntity<>(userService.saveUser(newUser), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserDTO newUser) {
        log.info("Получен PUT запрос по эндпоинту '/users' на обновление user");
        return new ResponseEntity<>(userService.updateUser(newUser), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> readAllUsers() {
        log.info("Получен GET запрос по эндпоинту '/users' на получение всех users");
        return new ResponseEntity<>(userService.readAllUsers(), HttpStatus.OK);
    }

    @PutMapping("{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFiend(id, friendId);
        log.info(
                "Получен PUT запрос по эндпоинту '/users/{}/friends/{}' на добавление friend c ID {} для user c ID {}. ",
                id, friendId, id, friendId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriendById(id, friendId);
        log.info(
                "Получен DELETE запрос по эндпоинту '/users/{}/friends/{}' " +
                        "на удаление friend c ID {} для user c ID {}.",
                id, friendId, id, friendId);
        return new ResponseEntity<>(HttpStatus.valueOf(200));
    }

    @GetMapping("{id}/friends")
    public ResponseEntity<List<UserDTO>> readAllFriendsByUserId(@PathVariable Long id) {
        log.info("Получен GET запрос по эндпоинту '/users/{id}/friends' на получение всех друзей для user c ID {}.",
                id);
        return new ResponseEntity<>(userService.readAllFriendsByUserId(id), HttpStatus.OK);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<List<UserDTO>> readAllCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info(
                "Получен GET запрос по эндпоинту '/users/{}/friends/common/{}' на получение всех общих друзей у Users c ID {} и {}.",
                id, otherId, id, otherId);
        return new ResponseEntity<>(userService.readAllCommonFriends(id, otherId), HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        log.info("Получен GET запрос по эндпоинту '/users/{userId}' на получение Users c ID {}", userId);
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.info("Получен DELETE запрос по эндпоинту '/users/{}' на удаление User c ID {}. ", userId, userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{id}/recommendations")
    public ResponseEntity<List<Film>> findRecommendation(@PathVariable Long id) {
        log.info("Получен GET запрос по эндпоинту '{}/recommendations' на получение recommendations для User c ID {}.",
                id, id);
        return new ResponseEntity<>(recommendationService.findRecommendation(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/feed")
    public ResponseEntity<List<EventDTO>> getUserFeed(@PathVariable(value = "id") Long id) {
        log.info("Получен GET запрос по эндпоинту '/users/{}/feed' на получение ленты событий User c ID {}.", id, id);
        return new ResponseEntity<>(feedService.getFeed(id), HttpStatus.OK);
    }
}
