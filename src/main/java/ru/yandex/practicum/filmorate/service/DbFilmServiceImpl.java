package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
@Service
@Primary
public class DbFilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFilmServiceImpl(FilmStorage filmStorage,
                             @Qualifier("userDbStorage") UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        if (count < 1)
            throw new ValidationException400("Запрошено количество популярных фильмов меньше одного: " + count);

        String sqlQuery = "SELECT f.FILM_ID," +
                "                f.NAME, \n" +
                "                f.DESCRIPTION, \n" +
                "                f.RELEASE_DATE, \n" +
                "                f.DURATION, \n" +
                "                f.GENRE, \n" +
                "                f.MPA,\n" +
                "                COUNT (fl.FILM_ID) AS FILM_LIKES\n" +
                "                FROM FILMS f \n" +
                "      LEFT JOIN FILMLIKES fl ON fl.FILM_ID = f.FILM_ID\n" +
                "      GROUP BY f.FILM_ID," +
                "                f.NAME, \n" +
                "                f.DESCRIPTION, \n" +
                "                f.RELEASE_DATE, \n" +
                "                f.DURATION, \n" +
                "                f.GENRE, \n" +
                "                f.MPA\n" +
                "                ORDER BY FILM_LIKES DESC\n" +
                "               LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, count);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        return null;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        return null;
    }

    @Override
    public Collection<Film> deleteAll() {
        return null;
    }
}
