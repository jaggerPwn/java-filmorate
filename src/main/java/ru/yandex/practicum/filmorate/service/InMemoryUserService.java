package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
public class InMemoryUserService implements UserService {

    UserStorage userStorage;

    public InMemoryUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Map<String, String> addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new ValidationException404("Id " + userId + " не найден в списке пользователей");
        } else if (friend == null) {
            throw new ValidationException404("Id " + friendId + " не найден в списке пользователей");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return Map.of("Success", "Now friends " + userId + " : " + friendId);
    }

    @Override
    public Map<String, String> deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new ValidationException404("Id " + userId + " не найден в списке пользователей");
        } else if (friend == null) {
            throw new ValidationException404("Id " + friendId + " не найден в списке пользователей");
        }

        if (!user.getFriends().contains(friendId) && !friend.getFriends().contains(userId))
            throw new ValidationException400(MessageFormat.format("{0} и {1} не друзья, удаление невозможно",
                    user.getName(), friend.getName()));
        else {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            return Map.of("Success", "Not friends anymore " + userId + " : " + friendId);
        }

    }

    @Override
    public UserStorage getUserStorage() {
        return userStorage;
    }

    @Override
    public Set<User> getUserFriends(int userId) {
        Set<Integer> friends = getUserStorage().getUserById(userId).getFriends();
        Set<User> userFriends = new TreeSet<>((o1, o2) -> {
            if (o1.getId() > o2.getId()) return 1;
            else if (o2.getId() > o1.getId()) return -1;
            return 0;
        });
        friends.stream()
                .map(friend -> getUserStorage().getUserById(friend)).forEach(userFriends::add);
        return userFriends;
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        Set<User> userFriends = getUserFriends(userId);
        Set<User> friendFriends = getUserFriends(friendId);
        Set<User> intersection = new HashSet<>(userFriends);
        intersection.retainAll(friendFriends);
        return intersection;
    }

    @Override
    public Map<Integer, User> deleteAll() {
        userStorage.getUsers().clear();
        userStorage.setId(0);
        return userStorage.getUsers();
    }
}