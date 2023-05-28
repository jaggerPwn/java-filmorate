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
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        String sqlQuery = "select \n" +
                "                f.FILM_ID, \n" +
                "                f.NAME, \n" +
                "                f.DESCRIPTION, \n" +
                "                f.RELEASE_DATE, \n" +
                "                f.DURATION,\n" +
                "                m.MPA_ID \n" +
                "                from FILMS f\n" +
                "                LEFT JOIN MPA_FILM mf ON f.FILM_ID = mf.FILM_ID \n" +
                "                LEFT JOIN MPA m ON mf.MPA_ID = m.MPA_ID";
        List<Film> query = jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm);

        setGenresForFilms(query);
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
        film.setId(keyHolder.getKey().intValue());
        setUpMpaAndGeneres(film);


        return film;
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
        return film;
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
        selectMpaNGenre(filmId, film);
        return film;
    }

    private void selectMpaNGenre(int filmId, Film film) {
        String sqlQueryForGenre = "SELECT g.GENRE_ID, g.NAME  FROM GENRES g LEFT JOIN FILMS f ON f.FILM_ID = g.FILM_ID WHERE f.FILM_ID = ?";
        try {
            Collection<Genres> genresCollection = jdbcTemplate.query(sqlQueryForGenre, FilmDbStorage::mapRowToGenre, filmId);
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
        Mpa mpa = Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
        return mpa;
    }

    @Override
    public void clear() {
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

    public static Genres mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genres genres = Genres.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
        return genres;
    }

    private void setUpMpaAndGeneres(Film film) {
        String sqlForGenre = "INSERT INTO GENRES (GENRE_ID, NAME, FILM_ID) VALUES (?, ?, ?)";
        try {
            for (Genres genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenre, genre.getId(), genre.getName(), film.getId());
            }
        } catch (DataAccessException | NullPointerException ignored) {
        }
        int update;
        try {
            String sqlForMPA = "INSERT INTO MPA (MPA_ID, NAME, FILM_ID) VALUES (?, ?, ?)";
            update = jdbcTemplate.update(sqlForMPA, film.getMpa().getId(), film.getMpa().getName(), film.getId());
        } catch (DataAccessException | NullPointerException ignored) {
            ignored.printStackTrace();
        }
    }


    private void updateUpMpaAndGeneres(Film film) {
        String sqlForGenre = "UPDATE GENRES_FILM SET GENRE_ID = ? WHERE FILM_ID = ?";
        try {
            for (Genres genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenre, genre.getId(), film.getId());
            }
        } catch (DataAccessException | NullPointerException ignored) {
            String sqlForGenreDel = "DELETE FROM GENRES_FILM WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlForGenreDel, film.getId());
        }
        int update;
        try {
            String sqlForMPA = "UPDATE MPA_FILM SET  MPA_ID = ?, FILM_ID = ?";
            update = jdbcTemplate.update(sqlForMPA, film.getMpa().getId(),film.getId());
        } catch (DataAccessException | NullPointerException ignored) {
            ignored.printStackTrace();
        }
    }

    private void setGenresForFilms(List<Film> query) {
        String sqlMapFilmToGenre = "select \n" +
                "                   gf.FILM_ID, \n" +
                "                   g.NAME, \n" +
                "                   g.GENRE_ID \n" +
                "                   from FILMS f\n" +
                "                   JOIN GENRES_FILM gf ON gf.FILM_ID = f.FILM_ID\n" +
                "                   JOIN GENRES g ON gf.GENRE_ID = g.GENRE_ID ";
        Collection<Genres> genres = new ArrayList<>();
        var stringObjectMap = jdbcTemplate.queryForList(sqlMapFilmToGenre);

        Map<ArrayList<Integer>, Genres> filmAllGenres = new HashMap<>();
        for (Map<String, Object> map : stringObjectMap) {
            Integer genId = 0;
            Integer filmId = 0;
            String genName = null;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey().equals("FILM_ID")) filmId = Integer.parseInt(String.valueOf(entry.getValue()));
                if (entry.getKey().equals("NAME")) genName = genName = String.valueOf(entry.getValue());
                if (entry.getKey().equals("GENRE_ID")) genId = Integer.parseInt(String.valueOf(entry.getValue()));
            }
            Genres genres1 = Genres.builder().id(genId).name(genName).build();
            Integer finalFilmId = filmId;
            Film film1 = query.stream().filter(film -> film.getId() == finalFilmId).findFirst().get();
            film1.getGenres().add(genres1);
        }
    }

    private boolean filmDateIsBefore(Film film) {

        return film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
