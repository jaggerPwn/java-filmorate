package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class GenreDBStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre readById(Integer id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";

        if (id == null) {
            throw new ValidationException("Невозможно выполнить запрос с пустым аргументом.");
        }
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapToGenre, id);
        } catch (Throwable e) {
            throw new ResponseStatusException(HttpStatus.valueOf(404), "Нет такого Genre");
        }
    }

    @Override
    public List<Genre> readAll() {
        String sqlQuery = "SELECT * FROM genres ORDER BY id";
        log.debug("Все Genres получены.");
        return jdbcTemplate.query(sqlQuery, this::mapToGenre);
    }

    public Genre mapToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    public Set<Genre> getGenresByFilmID(Long filmId) {
        String sqlQuery
                = "SELECT genreid, name FROM filmgenres INNER JOIN genres ON genreid = id WHERE filmid = ? ORDER BY genreid ASC ";
        List<Genre> genres = jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> new Genre(rs.getInt("genreid"), rs.getString("name")), filmId);
        log.debug("Получен список Genres  для Film с id {}", filmId);
        return new HashSet<>(genres);
    }
}
