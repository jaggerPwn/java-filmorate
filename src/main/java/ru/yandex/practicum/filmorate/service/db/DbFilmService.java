package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.db.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Primary
public class DbFilmService implements FilmService {

    private final Storage<Film> filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFilmService(Storage<Film> storage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = storage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Storage<Film> getFilmStorage() {
        return filmStorage;
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        if (count < 1)
            throw new ValidationException400("Запрошено количество популярных фильмов меньше одного: " + count);

        String sqlQuery = "SELECT             f.FILM_ID, \n" +
                "                             f.NAME, \n" +
                "                             f.DESCRIPTION, \n" +
                "                             f.RELEASE_DATE, \n" +
                "                             f.DURATION,\n" +
                "                             COUNT (fl.FILM_ID) AS FILM_LIKES,\n" +
                "                             m.MPA_ID   \n" +
                "                             FROM FILMS f \n" +
                "                   LEFT JOIN FILMLIKES fl ON fl.FILM_ID = f.FILM_ID\n" +
                "                   LEFT JOIN MPA_FILM mf ON mf.FILM_ID  = f.FILM_ID \n" +
                "                   LEFT JOIN MPA m ON m.MPA_ID  = mf.MPA_ID \n" +
                "                   GROUP BY f.FILM_ID, \n" +
                "                              f.NAME, \n" +
                "                             f.DESCRIPTION, \n" +
                "                             f.RELEASE_DATE,\n" +
                "                             FL.USER_ID,\n" +
                "                             f.DURATION,\n" +
                "                             m.MPA_ID\n" +
                "                             ORDER BY FILM_LIKES DESC\n" +
                "                            LIMIT ?";
        return jdbcTemplate.query(sqlQuery, DbFilmStorage::mapRowToFilm, count);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        String sqlQuery = "SELECT FILM_ID, USER_ID FROM FILMLIKES f WHERE FILM_ID = ? AND USER_ID = ?;";
        Boolean hasLike;
        try {
            hasLike = jdbcTemplate.queryForObject(sqlQuery, DbFilmService::mapGetFilmlike, filmId, userId);
            if (Boolean.TRUE.equals(hasLike)) {
                throw new ValidationException404("USER " + userId + " already added like to film " + filmId);
            }
        } catch (DataAccessException ignored) {
        }

        sqlQuery = "INSERT INTO FILMLIKES (FILM_ID, USER_ID)  VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return filmStorage.getById(filmId);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM FILMLIKES WHERE FILM_ID = ? AND USER_ID = ?";
        int update = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (update != 1) throw new ValidationException404(String.format("Like not found in pair USER %d," +
                " FILM %d", userId, filmId));
        return filmStorage.getById(filmId);
    }

    @Override
    public Collection<Film> deleteAll() {
        String sqlQuery = "DELETE FROM MPA_FILM";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "DELETE FROM GENRES_FILM";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "DELETE FROM FILMLIKES";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "DELETE FROM FILMS";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);

        return filmStorage.findAll();
    }

    @Override
    public Mpa getMpa(Integer mpaID) {
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sqlQuery, this::makeMPAlist, mpaID);
        } catch (DataAccessException e) {
            throw new ValidationException404("MPA " + mpaID + " not found");
        }
        return mpa;
    }

    @Override
    public Collection<Mpa> getMpa() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, this::makeMPAlist);
    }

    @Override
    public Genres getGenres(Integer genresId) {
        String sqlQuery = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        Genres genres;
        try {
            genres = jdbcTemplate.queryForObject(sqlQuery, this::makeGenreslist, genresId);
        } catch (DataAccessException e) {
            throw new ValidationException404("GENRE " + genresId + " not found");
        }
        return genres;
    }

    @Override
    public Collection<Genres> getGenres() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, this::makeGenreslist);
    }

    private Mpa makeMPAlist(ResultSet resultSet, int i) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    private Genres makeGenreslist(ResultSet resultSet, int i) throws SQLException {
        return Genres.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    private static boolean mapGetFilmlike(ResultSet resultSet, int id) throws SQLException {
        ArrayList<Integer> rows = new ArrayList<>();
        rows.add(resultSet.getInt("FILM_ID"));
        rows.add(resultSet.getInt("USER_ID"));
        return rows.get(0) != null && rows.get(1) != null;
    }
}
