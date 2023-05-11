package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.exception.ValidationException500;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final UserStorage userStorage;
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Film create(Film film) {
        if (filmDateIsBefore(film)) {
            throw new ValidationException400("дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }


    @Override
    public Film put(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException500("No such film id: " + film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int filmId) {
        if (films.get(filmId) == null) throw new ValidationException404("Фильм " + filmId + "не найден");
        return films.get(filmId);
    }

    private boolean filmDateIsBefore(Film film) {

        return film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
