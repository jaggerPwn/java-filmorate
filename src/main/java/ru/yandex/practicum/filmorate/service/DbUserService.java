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
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
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
        if (userIdDB == friendIdDB) throw new ValidationException400("User cannot invite himself");
        String sqlUserFriendArray;
        String sqlFriendFriendsArray;
        String sqlAddFriend;
        String sqlUpdateToAccepted;

        int friendAcceptanceStatus = -1;
        int userAcceptanceStatus = -1;

        Integer[] userFriendArray = new Integer[3];
        Integer[] friendFriendsArray = new Integer[3];

        try {
            sqlUserFriendArray = "SELECT USER_ID, FRIEND_ID, STATUS FROM FRIENDS WHERE USER_ID IN (?) AND FRIEND_ID IN (?) AND STATUS IN (0)";
            userFriendArray = jdbcTemplate.queryForObject(sqlUserFriendArray, this::mapRowToSimpleArray, userId, friendId);
        } catch (DataAccessException ignored) {
        }
        try {
            sqlFriendFriendsArray = "SELECT USER_ID, FRIEND_ID, STATUS FROM FRIENDS WHERE USER_ID IN (?) AND FRIEND_ID IN (?) AND STATUS IN (0)";
            friendFriendsArray = jdbcTemplate.queryForObject(sqlFriendFriendsArray, this::mapRowToSimpleArray, friendId, userId);
        } catch (DataAccessException ignored) {
        }
        if (userFriendArray[0] == null && friendFriendsArray[0] == null) {
            sqlAddFriend = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID, STATUS) VALUES (?, ?, 1)";  //ЕСЛИ НУЖНА АВТОРИЗАЦИЯ НА ДРУЗЬЯ ПОМЕНЯТЬ ПОСЛЕДНЕЕ ЗНАЧЕНИЕ НА 0
            userAcceptanceStatus = jdbcTemplate.update(sqlAddFriend, userId, friendId);
            return Map.of("Success", String.format(MessageFormat.format("invitation sent userAcceptanceStatus = {0}, friendAcceptanceStatus = {1}", userAcceptanceStatus, friendAcceptanceStatus)));
        }
        else if (userFriendArray[0] != null) {
            throw new ValidationException400(userId + " already sent invitation to " + friendId);
        }
        else {
            if (friendFriendsArray[2] == 1)
                throw new ValidationException400(userId + " already friends with " + friendId);
            sqlUpdateToAccepted = "UPDATE FRIENDS f SET STATUS = 1 WHERE f.USER_ID IN (?) AND f.FRIEND_ID IN (?) AND STATUS IN (0)";
            friendAcceptanceStatus = jdbcTemplate.update(sqlUpdateToAccepted, friendId, userId);
            userAcceptanceStatus = 1;
            return Map.of("Success", String.format(MessageFormat.format("now friends userAcceptanceStatus = {0}, friendAcceptanceStatus = {1}", userAcceptanceStatus, friendAcceptanceStatus)));
        }
    }

    @Override
    public Map<String, String> deleteFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return null;
    }

    @Override
    public UserStorage getUserStorage() {
        return userStorage;
    }

    @Override
    public Collection<User> getUserFriends(int userId) {
        String sqlQuery = "select u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY\n" +
                "FROM USERS u \n" +
                "LEFT JOIN FRIENDS f ON f.FRIEND_ID = u.USER_ID \n" +
                "WHERE f.USER_ID = ? AND f.STATUS = 1";
        List<User> friendList = jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, userId);
        return friendList;
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        String sqlQuery = "select u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY\n" +
                "FROM USERS u \n" +
                "LEFT JOIN FRIENDS f ON f.FRIEND_ID = u.USER_ID \n" +
                "WHERE f.USER_ID = ? AND f.STATUS = 1";
        List<User> friendList1 = jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, userId);
        List<User> friendList2 = jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, friendId);
        Set<User> intersection = new HashSet<>(friendList1);
        intersection.retainAll(friendList2);
        return intersection;
    }

    @Override
    public Collection<User> deleteAll() {
        String sqlQuery = "DELETE FROM FRIENDS";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "DELETE FROM USERS";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE  USERS  ALTER COLUMN USER_ID \n" +
                " RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        return userStorage.findAll();
    }

    private Integer[] mapRowToSimpleArray(ResultSet resultSet, int i) {
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
