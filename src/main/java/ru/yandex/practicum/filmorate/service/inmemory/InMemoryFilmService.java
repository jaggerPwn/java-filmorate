package ru.yandex.practicum.filmorate.service.inmemory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;

@Service
public class InMemoryFilmService implements FilmService {
    private final Storage<Film> filmStorage;
    private final Storage<User> userStorage;

    @Autowired
    public InMemoryFilmService(Storage<Film> filmStorage,
                               @Qualifier("inMemoryUserStorage") Storage<User> userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Storage<Film> getFilmStorage() {
        return filmStorage;
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        if (count < 1)
            throw new ValidationException400("Запрошено количество популярных фильмов меньше одного: " + count);
        Collection<User> all = userStorage.findAll();
        Map<Film, Integer> allFilmLikes = new HashMap<>();

        for (User user : all) {
            for (Film film : user.getFilmLikes()) {
                if (!allFilmLikes.containsKey(film)) allFilmLikes.put(film, 1);
                else allFilmLikes.put(film, allFilmLikes.get(film) + 1);
            }
        }
        return allFilmLikes.keySet();
    }

    @Override
    public Film addLike(int filmId, int userId) {
        User user = userStorage.getById(userId);
        Film film = filmStorage.getById(filmId);
        if (user.getFilmLikes().contains(film))
            throw new ValidationException404("Пользователь " + userId + " уже лайкал фильм " + filmId);
        else {
            user.getFilmLikes().add(film);
        }
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        User user = userStorage.getById(userId);
        Film film = filmStorage.getById(filmId);
        if (!user.getFilmLikes().contains(film))
            throw new ValidationException404("Пользователь " + userId + " не лайкал фильм " + filmId);
        else {
            user.getFilmLikes().remove(film);
        }
        return film;
    }

    @Override
    public Collection<Film> deleteAll() {
        filmStorage.findAll().clear();
        return filmStorage.findAll();
    }

    @Override
    public Mpa getMpa(Integer mpaID) {
        return null;
    }

    @Override
    public Collection<Mpa> getMpa() {
        return null;
    }

    @Override
    public Genres getGenres(Integer genresId) {
        return null;
    }

    @Override
    public Collection<Genres> getGenres() {
        return null;
    }
}