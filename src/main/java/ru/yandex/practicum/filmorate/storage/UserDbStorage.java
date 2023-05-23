package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY from USERS";

        return jdbcTemplate.query(sqlQuery, UserDbStorage.this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        String sqlQuery = "insert into USERS(EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public void setId(int id) {

    }

    @Override
    public User update(User user) {
        String sqlQuery = "update USERS set " +
                "USER_ID = ?, EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "where USER_ID = " + user.getId();
        jdbcTemplate.update(sqlQuery
                , user.getId()
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday());
        return user;
    }

    @Override
    public User getUserById(int userId) {
        String sqlQuery = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY from USERS where USER_ID =  ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public void clear() {
        jdbcTemplate.update("delete from USERS where USER_ID > 3");
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}
