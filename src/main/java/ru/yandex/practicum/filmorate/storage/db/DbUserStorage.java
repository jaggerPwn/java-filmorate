package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.exception.ValidationException500;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Repository
public class DbUserStorage implements Storage<User> {
    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY from USERS";

        return jdbcTemplate.query(sqlQuery, DbUserStorage::mapRowToUser);
    }

    @Override
    public User create(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException400("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
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
        try {
            user.setId(keyHolder.getKey().intValue());
        } catch (InvalidDataAccessApiUsageException | NullPointerException e) {
            e.printStackTrace();
            throw new ValidationException500("key not assigned");
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException500("Дата рождения не может быть в будущем");
        }

        String sqlQuery = "update USERS set " +
                "USER_ID = ?, EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "where USER_ID = ?";
        int update = jdbcTemplate.update(sqlQuery, user.getId(), user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());
        if (update < 1) throw new ValidationException404("user" + user.getId() + " not found");
        return user;
    }

    @Override
    public User getById(int userId) {
        String sqlQuery = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY from USERS where USER_ID =  ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, DbUserStorage::mapRowToUser, userId);
        } catch (DataAccessException e) {
            throw new ValidationException404("user " + userId + " not found");
        }
        return user;
    }

    @Override
    public void clear() {
        jdbcTemplate.update("delete from USERS");
    }

    public static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}
