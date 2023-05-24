package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(FilmStorage filmStorage,
                               //TODO заменить на inMemoryUserStorage
                               @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
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
        Set<Film> films = allFilmLikes.keySet();
        return (List<Film>) films;
    }

    @Override
    public Film addLike(int filmId, int userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (user.getFilmLikes().contains(film))
            throw new ValidationException404("Пользователь " + userId + " уже лайкал фильм " + filmId);
        else {
            user.getFilmLikes().add(film);
        }
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
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
        filmStorage.setId(0);
        return filmStorage.findAll();
    }
}