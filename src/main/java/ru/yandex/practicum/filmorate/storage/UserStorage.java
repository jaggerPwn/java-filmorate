package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User saveUser(User user);

    User updateUser(User user);

    List<User> readAllUsers();

    User getUserById(Long id);

    void userAddFriend(Long userId, Long friendId);

    void userDeleteFriend(Long userId, Long friendId);

    List<User> getAllFriendByUserId(Long id);

    void deleteUser(Long id);
}
