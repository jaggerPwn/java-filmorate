package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class LikeDBStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void deleteLike(Long filmId, Long userId) {
        String query = "DELETE FROM likes WHERE filmid = ? AND userid = ?";
        jdbcTemplate.update(query, filmId, userId); // в том же порядке что и в скрипте
        log.debug("Like у Film с id {} от пользователся с ID {} удалён", filmId, userId);
    }

    public void addLike(Long filmId, Long userId) {
        String query = "INSERT INTO likes (filmid, userid) VALUES  (?,?) ";
        jdbcTemplate.update(query, filmId, userId);
        log.debug("Film с ID {} добавлен Like от пользователя с ID {}", filmId, userId);
    }


    public Set<Long> getLikerByFilmId(Long filmID) {
        String query = "SELECT userid FROM likes WHERE filmid = ?";
        List<Long> likes = jdbcTemplate.query(query, (rs, rowNum) -> rs.getLong("userid"), filmID);
        log.debug("Получен список userid Like у Film с id {}.", filmID);
        return new HashSet<>(likes);
    }

}
