package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("filmDBStorage")
public class FilmDBStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDBStorage mpaDBStorage;
    private final GenreStorage genreStorage;
    private final LikeDBStorage likeDBStorage;

    @Autowired
    public FilmDBStorage(JdbcTemplate jdbcTemplate, MpaDBStorage mpaDBStorage, GenreStorage genreStorage, LikeDBStorage likeDBStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDBStorage = mpaDBStorage;
        this.genreStorage = genreStorage;
        this.likeDBStorage = likeDBStorage;
    }

    @Override
    public Film saveFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films").usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(film.filmToMap());
        film.setId((Long) key);
        film.setMpa(mpaDBStorage.readById(film.getMpa().getId()));

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String query = "INSERT INTO filmgenres (filmid,genreid) VALUES (?,?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(query, film.getId(), genre.getId());
            }
        }
        log.debug("Film c ID {} сохранён.", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Film не найден");
        }
        String sqlQuery = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, mpaid = ? WHERE id = ?";
        if (jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId()) != 0) {
            log.info("Film c id {} обновлён", film.getId());
        } else {
            throw new EntityNotFoundException("Film с таким id не существует");
        }

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            String sqlDelete = "DELETE FROM filmgenres WHERE filmid = ?";
            jdbcTemplate.update(sqlDelete, film.getId());

        } else {
            String sqlDelete2 = "DELETE FROM filmgenres WHERE filmid = ?";
            jdbcTemplate.update(sqlDelete2, film.getId());
            String sqlUpdate = "INSERT INTO filmgenres (filmid, genreid) VALUES (?,?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlUpdate, film.getId(), genre.getId());
            }
        }
        film.setGenres(genreStorage.getGenresByFilmID(film.getId()));
        return film;
    }

    @Override
    public List<Film> readAllFilms() {
        String query = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(query, this::mapToFilm);
        log.debug("Получены все Film.");
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        String query = "SELECT * FROM films WHERE id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query, id);
        if (sqlRowSet.first()) {
            Film film = Film.builder()
                    .id(sqlRowSet.getLong("id"))
                    .name(sqlRowSet.getString("name"))
                    .description(sqlRowSet.getString("description"))
                    .releaseDate(sqlRowSet.getDate("releaseDate").toLocalDate())
                    .duration(sqlRowSet.getInt("duration"))
                    .genres(genreStorage.getGenresByFilmID(sqlRowSet.getLong("id")))
                    .likes(likeDBStorage.getLikerByFilmId(sqlRowSet.getLong("id")))
                    .mpa(mpaDBStorage.readById(sqlRowSet.getInt("mpaid")))
                    .build();
            log.debug("Получен Film с ID {}.", id);
            return film;
        } else {
            throw new EntityNotFoundException("Film не найден");
        }
    }

    @Override
    public Set<Film> getTopFilms(Long count) {
        String query = "SELECT id, name, description, releaseDate, duration, mpaid FROM films " +
                "LEFT JOIN likes ON id = filmid GROUP BY id ORDER BY count(userid) desc  LIMIT ?";
        List<Film> topFilms = jdbcTemplate.query(query, this::mapToFilm, count);
        log.debug("Получаем топ {} Film по кол-ву Likes.", count);
        return new HashSet<>(topFilms);
    }

    public Film mapToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaDBStorage.readById(rs.getInt("mpaid")))
                .likes(likeDBStorage.getLikerByFilmId(rs.getLong("id")))
                .genres(genreStorage.getGenresByFilmID(rs.getLong("id")))
                .build();
    }

}
