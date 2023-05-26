package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "select " +
                "FILM_ID, " +
                "NAME, " +
                "DESCRIPTION, " +
                "RELEASE_DATE, " +
                "DURATION, " +
                "UPPER(GENRE) as GENRE, " +
                "UPPER(RATING) as RATING" +
                " from FILMS";

        return jdbcTemplate.query(sqlQuery, FilmDbStorage.this::mapRowToFilm);
    }

    @Override
    public void setId(int id) {}

    @Override
    public Film create(Film film) {
        if (filmDateIsBefore(film)) {
            throw new ValidationException400("release date must be no earlier than December 28, 1895");
        }
        System.out.println(String.valueOf(film.getGenre()));
        String sqlQuery = "insert into FILMS(" +
                "NAME, " +
                "DESCRIPTION, " +
                "RELEASE_DATE, " +
                "DURATION, " +
                "GENRE, " +
                "RATING" +
                ") " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setString(5, String.valueOf(film.getGenre()));
            statement.setString(6, String.valueOf(film.getRating()));
            return statement;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update FILMS set " +
                "FILM_ID = ?, " +
                "NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ?, " +
                "GENRE = ?, " +
                "RATING = ? " +
                "where FILM_ID = ?";
        int update = jdbcTemplate.update(sqlQuery
                , film.getId()
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , String.valueOf(film.getGenre())
                , String.valueOf(film.getRating())
                , film.getId()
        );
        if(update < 1 ) {
            throw new ValidationException404("film not found");
        }
        return film;
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "select FILM_ID," +
                "NAME, " +
                "DESCRIPTION, " +
                "RELEASE_DATE, " +
                "DURATION, " +
                "GENRE, " +
                "RATING " +
                "from FILMS where FILM_ID =  ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } catch (DataAccessException e) {
            throw new ValidationException404("film not found");
        }
        return film;
    }

    @Override
    public void clear() {
        jdbcTemplate.update("delete from FILMS where FILM_ID > 3");
    }


    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .build();
        try {
            film.setGenre(Genre.valueOf(resultSet.getString("GENRE")));
        } catch (IllegalArgumentException | SQLException ignored) {
        }
        try {
            film.setRating(Rating.valueOf(resultSet.getString("RATING")));
        } catch (IllegalArgumentException | SQLException ignored) {
        }
        return film;
    }

    private boolean filmDateIsBefore(Film film) {

        return film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
