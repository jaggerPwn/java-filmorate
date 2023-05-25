package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Primary
public class DbUserService implements UserService {
    UserStorage userStorage;
    JdbcTemplate jdbcTemplate;


    @Autowired
    public DbUserService(@Qualifier("userDbStorage") UserStorage userStorage
            , JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Map<String, String> addFriend(int userId, int friendId) {
        String sqlQuery1 = "SELECT USER_ID FROM USERS u WHERE USER_ID = ?";
        try {
            Integer userIdDB = jdbcTemplate.queryForObject(sqlQuery1, Integer.class, userId);
        } catch (DataAccessException e) {
            throw new ValidationException404("user not found, ID " + userId);
        }
        try {
            Integer userIdDB = jdbcTemplate.queryForObject(sqlQuery1, Integer.class, friendId);
        } catch (DataAccessException e) {
            throw new ValidationException404("friend not found, ID " + friendId);
        }

        String sqlUserFriendArray = "SELECT USER_ID, FRIEND_ID, STATUS FROM FRIENDS f WHERE USER_ID = ?";
        Integer[] friendArray = null;
        try {
            friendArray = jdbcTemplate.queryForObject(sqlUserFriendArray, this::mapRowToArray, userId);
        } catch (DataAccessException ignored) {
            throw new RuntimeException(ignored);
        }
        if (friendArray != null) {
            if (friendArray[2] == 1) throw new ValidationException400(userId + " already friends with " + friendId);
            else if (friendArray[2] == 0)
                throw new ValidationException400(userId + " already sent invitation for user " + friendId);
        } else {
            String sqlFriendFriendsArray = "SELECT USER_ID, FRIEND_ID, STATUS FROM FRIENDS f WHERE USER_ID = ?";
            Integer[] friendFriendsArray = null;


            String sqlAddFriend = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID, STATUS) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlAddFriend, userId, friendId, 0);
        }
        return Map.of("Success", String.format("Friend invitation sent from %d to %d", userId, friendId));
    }

    @Override
    public Map<String, String> deleteFriend(int userId, int friendId) {
        return null;
    }

    @Override
    public UserStorage getUserStorage() {
        return null;
    }

    @Override
    public Set<User> getUserFriends(int userId) {
        return null;
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        return null;
    }

    @Override
    public Collection<User> deleteAll() {
        return null;
    }

    private Integer[] mapRowToArray(ResultSet resultSet, int i) {
        Integer[] integers = new Integer[3];
        try {
            integers[0] = resultSet.getInt("USER_ID");
            integers[1] = resultSet.getInt("FRIEND_ID");
            integers[2] = resultSet.getInt("STATUS");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return integers;
    }
}
