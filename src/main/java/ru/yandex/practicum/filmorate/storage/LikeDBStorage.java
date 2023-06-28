package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class LikeDBStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String query = "DELETE FROM likes WHERE filmid = ? AND userid = ?";
        jdbcTemplate.update(query, filmId, userId); // в том же порядке что и в скрипте
        log.debug("Like у Film с id {} от User с ID {} удалён", filmId, userId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String query = "DELETE FROM likes WHERE filmId = ? AND userId = ? ";
        jdbcTemplate.update(query, filmId, userId);
        query = "INSERT INTO likes (filmid, userid) VALUES(?, ?) ";
        jdbcTemplate.update(query, filmId, userId);
        log.debug("Film с ID {} добавлен Like от User с ID {}", filmId, userId);
    }

    @Override
    public Set<Long> getLikerByFilmId(Long filmID) {
        String query = "SELECT userid FROM likes WHERE filmid = ?";
        List<Long> likes = jdbcTemplate.query(query, (rs, rowNum) -> rs.getLong("userid"), filmID);
        log.debug("Получен список userid Like у Film с id {}.", filmID);
        return new HashSet<>(likes);
    }

    @Override
    public List<Long> getUsersWithSameLikes(Long userId) {
        final String sqlQuery = "SELECT fl.userid, count(fl.filmid) rate from likes ul" +
                " join likes fl  on (ul.filmid = fl.filmid and ul.userid != fl.userid)" +
                " join users u  on (fl.userid != u.id)" +
                " where  ul.userid = ? " +
                " group by fl.userid " +
                " having rate > 1 " +
                " order by rate desc" +
                " limit 10";
        List<Long> usersForLike = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("USERID"), userId);
        log.debug("Получен список userid с похожими likes для User с ID {}.", userId);
        return usersForLike;
    }

    @Override
    public List<Long> getFilmRecommendationsFrom(Long userId, List<Long> sameUserIds) {
        String inSql = String.join(",", Collections.nCopies(sameUserIds.size(), "?"));
        final String sqlQuery = "select fl.filmid from likes fl " +
                " where  fl.userid in (" + inSql + ")" +
                " and fl.filmid not in (select ul.filmid from likes ul where ul.userid = ?) ";
        List<Long> idFilmsOfRec = jdbcTemplate.query(sqlQuery, LikeDBStorage::mapRow, sameUserIds.toArray(), userId);
        log.debug("Получен список с Films id для рекомендаций User с ID {}.", userId);
        return idFilmsOfRec;
    }

    private static Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("FILMID");
    }

}
