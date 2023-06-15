package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.db.*;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Primary
public class DbUserService implements UserService {
    Storage<User> userStorage;
    JdbcTemplate jdbcTemplate;


    @Autowired
    public DbUserService(@Qualifier("dbUserStorage") Storage<User> userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery1 = "SELECT USER_ID FROM USERS u WHERE USER_ID = ?";
        Integer userIdDB;
        Integer friendIdDB;
        try {
            userIdDB = jdbcTemplate.queryForObject(sqlQuery1, Integer.class, userId);
        } catch (DataAccessException e) {
            throw new ValidationException404("user not found, ID " + userId);
        }
        try {
            friendIdDB = jdbcTemplate.queryForObject(sqlQuery1, Integer.class, friendId);
        } catch (DataAccessException e) {
            throw new ValidationException404("friend not found, ID " + friendId);
        }
        if (Objects.equals(userIdDB, friendIdDB)) throw new ValidationException400("User cannot invite himself");

        String sqlAddFriend;
        sqlAddFriend = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddFriend, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public Collection<User> getUserFriends(int userId) {
        String sqlQuery = "select u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY\n" +
                "FROM USERS u \n" +
                "LEFT JOIN FRIENDS f ON f.FRIEND_ID = u.USER_ID \n" +
                "WHERE f.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, DbUserStorage::mapRowToUser, userId);
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        String sqlQuery = "select u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY\n" +
                "FROM USERS u \n" +
                "LEFT JOIN FRIENDS f ON f.FRIEND_ID = u.USER_ID \n" +
                "WHERE f.USER_ID = ?";
        List<User> friendList1 = jdbcTemplate.query(sqlQuery, DbUserStorage::mapRowToUser, userId);
        List<User> friendList2 = jdbcTemplate.query(sqlQuery, DbUserStorage::mapRowToUser, friendId);
        Set<User> intersection = new HashSet<>(friendList1);
        intersection.retainAll(friendList2);
        return intersection;
    }

    @Override
    public Collection<User> deleteAll() {
        String sqlQuery = "DELETE FROM FRIENDS";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "DELETE FROM FILMLIKES";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "DELETE FROM USERS";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE  USERS  ALTER COLUMN USER_ID  RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        return userStorage.findAll();
    }

    @Override
    public Storage<User> getUserStorage() {
        return userStorage;
    }

    private Integer[] mapRowToSimpleArray(ResultSet resultSet, int i) {
        Integer[] integers = new Integer[3];
        try {
            integers[0] = resultSet.getInt("USER_ID");
            integers[1] = resultSet.getInt("FRIEND_ID");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return integers;
    }
}
