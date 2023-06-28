package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDBStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director saveDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("directors")
                .usingGeneratedKeyColumns("directorid");
        Number key = simpleJdbcInsert.executeAndReturnKey(directorToMap(director));
        director.setId((Long) key);
        log.debug("Director создан с ID {}.", director.getId());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE directorid = ?";
        if (jdbcTemplate.update(sqlQuery, director.getName(), director.getId()) != 0) {
            log.debug("Director {} успешно обновлён", director.getId());
            return director;
        } else {
            log.debug("Director {} не обновлён", director.getId());
            throw new EntityNotFoundException("Director с таким id не найден");
        }
    }

    @Override
    public List<Director> readAllDirectors() {
        log.debug("Все Director получены");
        return jdbcTemplate.query("SELECT * FROM directors", this::mapToDirector);
    }

    @Override
    public Director getDirectorById(Long id) {
        String sqlQuery = "SELECT * FROM directors WHERE directorid = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sqlQuery, this::mapToDirector, id);
            log.debug("Director с ID {} получен.", id);
            return director;
        } catch (Throwable throwable) {
            throw new EntityNotFoundException("Director с таким id не найден");
        }
    }

    @Override
    public void deleteDirectorById(Long id) {
        String sqlQuery = "DELETE FROM directors WHERE directorid = ?";
        if (jdbcTemplate.update(sqlQuery, id) != 0) {
            log.info("Director с Id {} удалён.", id);
        } else {
            log.info("Director с Id {} не найден.", id);
        }
    }

    public Director mapToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("directorid"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public Set<Director> getDirectorsByFilmId(Long filmId) {
        String sqlQuery = "SELECT fd.directorid, name FROM filmdirectors AS fd INNER JOIN directors AS d " +
                "ON d.directorid = fd.directorid WHERE filmid = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Director(rs.getLong("directorid"), rs.getString("name")), filmId);
        log.debug("Получен список Directors для Film с id {}", filmId);
        return new HashSet<>(directors);
    }

    public Map<String, Object> directorToMap(Director director) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("name", director.getName());
        return temp;
    }

}
