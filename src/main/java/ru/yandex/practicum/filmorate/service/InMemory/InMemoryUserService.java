package ru.yandex.practicum.filmorate.service.InMemory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.exception.ValidationException500;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.MessageFormat;
import java.util.*;

@Service
public class InMemoryUserService implements UserService {
    UserStorage userStorage;

    @Autowired
    public InMemoryUserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new ValidationException404("Id " + userId + " not found in user list");
        } else if (friend == null) {
            throw new ValidationException404("Id " + friendId + " not found in user list");
        }

        if (friend.getFriends().get(userId) != null) {
            if (friend.getFriends().get(userId) != 0 &&
                    friend.getFriends().get(userId) != 1
            ) {
                throw new ValidationException500("wrong parameter in friend list of user " + friendId
                        + " please check the database");
            }


            if (user.getFriends().get(friendId) != null) {
                if (user.getFriends().get(friendId) != 0 && user.getFriends().get(friendId) != 1) {
                    throw new ValidationException500("wrong parameter in friend list of user " + userId
                            + " please check the database");
                }
                if (user.getFriends().get(friendId) == 0) {
                    throw new ValidationException400(
                            String.format("User %d already sent friend invitation for user %d", userId, friendId));
                }
                if (user.getFriends().get(friendId) == 1) {
                    throw new ValidationException400(String.format("User %d already friends with user %d", userId, friendId));
                }
            }
        }

        if (friend.getFriends().containsKey(userId) &&
                friend.getFriends().get(userId) == 0) {
            friend.getFriends().put(userId, 1);
            user.getFriends().put(friendId, 1);
        } else user.getFriends().put(friendId, 0);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new ValidationException404("Id " + userId + " not found in user list");
        } else if (friend == null) {
            throw new ValidationException404("Id " + friendId + " not found in user list");
        }

        if (!user.getFriends().containsKey(friendId))
            throw new ValidationException400(MessageFormat.format("{0} and {1} has not sent invitation, " +
                            "deletion is not possible",
                    user.getName(), friend.getName()));
        else {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        }

    }

    @Override
    public Collection<User> getUserFriends(int userId) {
        TreeMap<Integer, Integer> friends = userStorage.getUserById(userId).getFriends();
        Set<User> userFriends = new TreeSet<>((o1, o2) -> {
            if (o1.getId() > o2.getId()) return 1;
            else if (o2.getId() > o1.getId()) return -1;
            return 0;
        });
        for (Map.Entry<Integer, Integer> entry : friends.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            if (value == 1) userFriends.add(userStorage.getUserById(key));
        }
        return userFriends;
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        Set<User> userFriends = (Set<User>) getUserFriends(userId);
        Set<User> friendFriends = (Set<User>) getUserFriends(friendId);
        Set<User> intersection = new HashSet<>(userFriends);
        intersection.retainAll(friendFriends);
        return intersection;
    }

    @Override
    public Collection<User> deleteAll() {
        userStorage.clear();
        return userStorage.findAll();
    }

    @Override
    public UserStorage getUserStorage() {
        return userStorage;
    }
}