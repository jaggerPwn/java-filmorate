package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component("reviewDbStorage")
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmServiceImpl filmService;
    private final UserServiceImpl userService;


    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate, FilmServiceImpl filmService, UserServiceImpl userService) {
        this.jdbcTemplate = jdbcTemplate;

        this.filmService = filmService;
        this.userService = userService;
    }


    @Override
    public Review saveReview(Review review) {
        filmService.getFilmByID(review.getFilmId());
        userService.getUserById(review.getUserId());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(reviewToMap(review.getContent(),
                review.getIsPositive(), review.getUserId(), review.getFilmId()));
        review.setReviewId((Long) key);
        log.debug("Review на Film c ID {} от User c ID {} создан", review.getFilmId(), review.getUserId());
        return review;
    }

    @Override
    public Review getReviewById(Long id) {
        String sqlQuery = "SELECT ID, CONTENT, ISPOSITIVE, USERID, FILMID " +
                "FROM PUBLIC.REVIEWS where ID = ? ";
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sqlQuery, ReviewDbStorage::mapToReview, id);
        } catch (DataAccessException e) {
            throw new EntityNotFoundException("review " + id + " not found");
        }
        log.debug("Review с ID {} на Film c ID {} от User c ID {} получено",
                review.getReviewId(), review.getFilmId(), review.getUserId());
        return review;
    }

    @Override
    public List<Review> readAllReviews(Long filmId, Long count) {
        List<Review> reviews;
        if (filmId == null) {
            String sqlQuery = "SELECT R.ID, " +
                    "r.CONTENT, " +
                    "r.ISPOSITIVE, " +
                    "r.USERID, " +
                    "r.FILMID, " +
                    "COUNT(NULLIF(POSITIVE, false))-COUNT(NULLIF(POSITIVE,true)) useful " +
                    "FROM PUBLIC.REVIEWS r " +
                    "LEFT JOIN REVIEWLIKES rl ON rl.REVIEWID = r.ID " +
                    "GROUP BY R.ID " +
                    "ORDER BY useful desc " +
                    "LIMIT ?;";
            reviews = jdbcTemplate.query(sqlQuery, ReviewDbStorage::mapToReview, count);
        } else {
            String sqlQuery = "SELECT R.ID, " +
                    "r.CONTENT, " +
                    "r.ISPOSITIVE, " +
                    "r.USERID, " +
                    "r.FILMID,  " +
                    "COUNT(NULLIF(POSITIVE, false))-COUNT(NULLIF(POSITIVE,true)) useful " +
                    "FROM PUBLIC.REVIEWS r " +
                    "LEFT JOIN REVIEWLIKES rl ON rl.REVIEWID = r.ID " +
                    "WHERE FILMID = ? " +
                    "GROUP BY R.ID " +
                    "ORDER BY useful desc " +
                    "LIMIT ?;";
            reviews = jdbcTemplate.query(sqlQuery, ReviewDbStorage::mapToReview, filmId, count);
        }
        log.debug("Review на Film c ID {} получены.", filmId);
        return reviews;
    }

    @Override
    public void saveReviewLikesOrDislikes(Long reviewId, Long userId, boolean positive) {
        String sql = "MERGE INTO reviewLikes (REVIEWID, USERID, POSITIVE) VALUES(?, ?, ?) ";
        jdbcTemplate.update(sql, reviewId, userId, positive);
        log.debug("Реакция на отзывы c ID {} от User c ID {} сохранены", reviewId, userId);
    }

    @Override
    public void deleteReviewLikesOrDislikes(Long reviewId, Long userId, boolean positive) {
        String sql = "DELETE FROM PUBLIC.REVIEWLIKES " +
                "WHERE USERID=? AND REVIEWID=? AND POSITIVE=?";
        jdbcTemplate.update(sql, positive, userId, reviewId);
        log.debug("Удаление реакции на Review c ID {} от User c ID {} успешно выполнено", reviewId, userId);
    }

    @Override
    public Review updateReview(Review review) {
        //Здесь только 2 параметра обновляется, иначе не проходит постмен тесты.
        // Пояснения в посте наставника: https://app.pachca.com/chats?thread_id=1280887
        String sqlQuery = "UPDATE PUBLIC.REVIEWS  " +
                "SET CONTENT = ?, ISPOSITIVE = ?  " +
                "WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        Review reviewById = getReviewById(review.getReviewId());
        log.debug("Review c ID {} обновлен", review.getReviewId());
        return reviewById;
    }


    @Override
    public void deleteReviewById(Long id) {
        String sqlQuery = "DELETE FROM PUBLIC.REVIEWS WHERE ID=? ";
        jdbcTemplate.update(sqlQuery, id);
        log.debug("Review c ID {} удалён", id);
    }

    public static Review mapToReview(ResultSet resultSet, int i) {
        Review build = null;
        try {
            build = Review.builder()
                    .reviewId(resultSet.getLong("id"))
                    .content(resultSet.getString("content"))
                    .filmId(resultSet.getLong("filmId"))
                    .userId(resultSet.getLong("userId"))
                    .isPositive(resultSet.getBoolean("isPositive"))
                    .build();
        } catch (SQLException ignored) {
        }
        try {
            build.setUseful(resultSet.getInt("useful"));
        } catch (SQLException sqlException) {
            build.setUseful(0);
        }
        return build;
    }

    public Map<String, ?> reviewToMap(String content, boolean isPositive, long userId, long filmId) {
        Map<String, Object> temp = new HashMap<>();
        temp.put("content", content);
        temp.put("ispositive", isPositive);
        temp.put("userid", userId);
        temp.put("filmid", filmId);
        return temp;
    }

}
