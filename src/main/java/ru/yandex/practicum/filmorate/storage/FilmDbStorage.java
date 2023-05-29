package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Film> query = new ArrayList<>();
        String sqlQuery = "SELECT FILM_ID FROM FILMS";
        List<Integer> film_id = jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> resultSet.getInt("FILM_ID"));
        for (Integer id : film_id) {
            query.add(this.getFilmById(id));
        }
        return query;
    }


    @Override
    public void setId(int id) {
    }

    @Override
    public Film create(Film film) {
        if (filmDateIsBefore(film)) {
            throw new ValidationException400("release date must be no earlier than December 28, 1895");
        }

        String sqlQuery = "insert into FILMS(" +
                "NAME, " +
                "DESCRIPTION, " +
                "RELEASE_DATE, " +
                "DURATION" +
                ") " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            return statement;
        }, keyHolder);
        int id;
        try {
            id = keyHolder.getKey().intValue();
        } catch (InvalidDataAccessApiUsageException | NullPointerException e) {
            e.printStackTrace();
            throw new ValidationException400("BAD ID ");
        }

        film.setId(id);
        setUpMpaAndGeneres(film);
        return getFilmById(id);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update FILMS set " +
                "FILM_ID = ?, " +
                "NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ? " +
                "where FILM_ID = ?";
        int update = jdbcTemplate.update(sqlQuery
                , film.getId()
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getId()
        );
        if (update < 1) {
            throw new ValidationException404("film not found");
        }
        updateUpMpaAndGeneres(film);
        return this.getFilmById(film.getId());
    }


    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "select FILM_ID," +
                "NAME, " +
                "DESCRIPTION, " +
                "RELEASE_DATE, " +
                "DURATION " +
                "from FILMS where FILM_ID =  ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, FilmDbStorage::mapRowToFilm, filmId);
        } catch (DataAccessException e) {
            throw new ValidationException404("film not found");
        }
        if (film != null) {
            selectMpaNGenre(filmId, film);
        } else {throw new ValidationException404("film not found, id " + filmId);}
        return film;
    }

    private void selectMpaNGenre(int filmId, Film film) {
        String sqlQueryForGenre = "SELECT gf.GENRE_ID, g.NAME \n" +
                "FROM GENRES_FILM gf  \n" +
                "LEFT JOIN GENRES g  ON gf.GENRE_ID = g.GENRE_ID\n" +
                "WHERE gf.FILM_ID = ?";
        Collection<Genres> genresCollection;
        try {
            genresCollection = jdbcTemplate.query(sqlQueryForGenre, FilmDbStorage::mapRowToGenre, filmId);
            film.setGenres(genresCollection);
        } catch (DataAccessException ignored) {
        }
        String sqlQueryForMPA = "SELECT m.MPA_ID, m.NAME  \n" +
                "        FROM MPA m \n" +
                "        LEFT JOIN MPA_FILM mf ON m.MPA_ID = mf.MPA_ID\n" +
                "        LEFT JOIN FILMS f ON f.FILM_ID = mf.FILM_ID \n" +
                "        WHERE mf.FILM_ID = ?";
        try {
            List<Mpa> query = jdbcTemplate.query(sqlQueryForMPA, FilmDbStorage::mapRowToMpa, filmId);
            film.setMpa(query.get(0));
        } catch (DataAccessException | IndexOutOfBoundsException ignored) {
        }
    }

    private static Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    @Override
    public void clear() {
        jdbcTemplate.update("DELETE FROM GENRES_FILM");
        jdbcTemplate.update("DELETE FROM MPA_FILM");
        jdbcTemplate.update("DELETE FROM FILMS");
    }


    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        ArrayList<Genres> genres = new ArrayList<>();
        Film film = Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .genres(genres)
                .build();
        try {
            int mpa_id = resultSet.getInt("MPA_ID");
            Mpa mpa = Mpa.builder().id(mpa_id).build();
            film.setMpa(mpa);
        } catch (SQLException ignored) {
        }


        return film;
    }

    private void setUpMpaAndGeneres(Film film) {
        String sqlForGenre = "INSERT INTO GENRES_FILM (GENRE_ID, FILM_ID) VALUES ( ?, ?)";
        try {
            for (Genres genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenre, genre.getId(), film.getId());
            }
        } catch (DataAccessException | NullPointerException ignored) {
        }
        try {
            String sqlForMPA = "INSERT INTO MPA_FILM (MPA_ID, FILM_ID) VALUES ( ?, ?)";
            jdbcTemplate.update(sqlForMPA, film.getMpa().getId(), film.getId());
        } catch (DataAccessException | NullPointerException e) {
            e.printStackTrace();
            String sqlForMPA = "UPDATE  MPA_FILM SET MPA_ID = ? WHERE FILM_ID = ?";
            if (film.getMpa() != null) {
                jdbcTemplate.update(sqlForMPA, film.getMpa().getId(), film.getId());
            }
            else {throw new ValidationException404("No MPA found"); }
        }
    }


    private void updateUpMpaAndGeneres(Film film) {
        String sqlQueryForGenres;
        try {

            Collection<Genres> genres = film.getGenres();
            if (genres.size() == 0) {
                sqlQueryForGenres = "DELETE FROM GENRES_FILM WHERE FILM_ID = ?";
                jdbcTemplate.update(sqlQueryForGenres, film.getId());
            } else {
                sqlQueryForGenres = "DELETE FROM GENRES_FILM WHERE FILM_ID = ?";
                jdbcTemplate.update(sqlQueryForGenres, film.getId());
                List<Genres> collect = genres.stream().distinct().collect(Collectors.toList());
                for (Genres genre : collect) {

                    String sqlForGenre = "UPDATE MPA_FILM SET MPA_ID = ? WHERE FILM_ID = ?";
                    int update1 = jdbcTemplate.update(sqlForGenre, film.getMpa().getId(), film.getId());
                    if (update1 != 0) {
                        sqlQueryForGenres = "INSERT INTO GENRES_FILM (FILM_ID, GENRE_ID) VALUES (? , ?)";
                    jdbcTemplate.update(sqlQueryForGenres, film.getId(), genre.getId());}
                }
            }
        } catch (DataAccessException | NullPointerException ignored) {
        }

        try {
            String sqlForMPA = "UPDATE MPA_FILM SET MPA_ID = ? WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlForMPA, film.getMpa().getId(), film.getId());
        } catch (DataAccessException | NullPointerException ignored) {
        }

    }

    public static Genres mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genres.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    private boolean filmDateIsBefore(Film film) {

        return film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
