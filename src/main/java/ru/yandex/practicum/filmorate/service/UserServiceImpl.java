package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.LikeDBStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.Validator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage us;
    private final FeedService fs;
    private final LikeDBStorage likeDBStorage;
    private final FilmDBStorage filmDBStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDBStorage") UserStorage us, LikeDBStorage likeDBStorage,
            FilmDBStorage filmDBStorage, FeedService fs) {
        this.us = us;
        this.fs = fs;
        this.likeDBStorage = likeDBStorage;
        this.filmDBStorage = filmDBStorage;
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        User user = UserMapper.dtoToUser(userDTO);
        if (Validator.userValidator(user)) {
            log.debug("User c ID {} сохранён.", userDTO.getId());
            return UserMapper.userToDTO(us.saveUser(user));
        }
        throw new ValidationException("Валидация User " + userDTO + " не пройдена.");
    }

    @Override
    public UserDTO updateUser(UserDTO userDto) {
        User user = UserMapper.dtoToUser(userDto);
        if (Validator.userValidator(user)) {
            log.debug("У User c ID {} обновлён статус", userDto.getId());
            return UserMapper.userToDTO(us.updateUser(user));
        }
        throw new ValidationException("Валидация User " + userDto + " не пройдена.");
    }

    @Override
    public List<UserDTO> readAllUsers() {
        log.debug("Получен список всех Users");
        return UserMapper.listUsersToListDto(us.readAllUsers());
    }

    @Override
    public void addFiend(Long userId, Long friendId) {
        us.userAddFriend(userId, friendId);
        fs.saveFeed(userId, Instant.now().toEpochMilli(), EventType.FRIEND, Operation.ADD, friendId);
        log.debug("User c ID {} добавлен Friend (User) c ID {}.", userId, friendId);
    }

    @Override
    public UserDTO getUserById(Long id) {
        log.debug("User c ID c {} получен.", id);
        return UserMapper.userToDTO(us.getUserById(id));
    }

    @Override
    public void deleteFriendById(Long idUser, Long idFriend) {
        us.userDeleteFriend(idUser, idFriend);
        fs.saveFeed(idUser, Instant.now().toEpochMilli(), EventType.FRIEND, Operation.REMOVE, idFriend);
        log.debug("Дружба между User c ID {} и User с ID {} аннулирована.", idUser, idFriend);
    }

    @Override
    public List<UserDTO> readAllFriendsByUserId(Long idUser) {
        User user = us.getUserById(idUser);
        Set<Long> ids = user.getFriends();
        List<UserDTO> friends = new ArrayList<>();
        for (Long id : ids) {
            friends.add(UserMapper.userToDTO(us.getUserById(id)));
        }
        log.debug("Получен список друзей у User c ID {}.", idUser);
        return friends;
    }

    @Override
    public List<UserDTO> readAllCommonFriends(Long idUser1, Long idUser2) {
        Set<Long> ids = new HashSet<>(us.getUserById(idUser1).getFriends());
        ids.retainAll(us.getUserById(idUser2).getFriends());
        log.debug("Получен список общих друзей у User с ID {} и User c ID {}.", idUser1, idUser2);
        return ids.stream().map(us::getUserById).map(UserMapper::userToDTO).collect(Collectors.toList());
    }

    @Override
    public List<Film> findRecommendation(Long idUser) {
        List<Long> sameUserIds = likeDBStorage.getUsersWithSameLikes(idUser);
        if (sameUserIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> recommendations = likeDBStorage.getFilmRecommendationsFrom(idUser, sameUserIds);

        List<Film> films = new ArrayList<>();
        for (Long id : recommendations) {
            films.add(filmDBStorage.getFilmById(id));
        }
        log.debug("Получен список Films рекоменованных для User с ID {}", idUser);
        return films;
    }

    @Override
    public void deleteUser(Long id) {
        us.deleteUser(id);
    }

}
