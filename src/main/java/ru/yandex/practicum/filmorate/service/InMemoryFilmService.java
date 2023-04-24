package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException400;
import ru.yandex.practicum.filmorate.exception.ValidationException404;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public InMemoryFilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        if (count < 1)
            throw new ValidationException400("Запрошено количество популярных фильмов меньше одного: " + count);
        Collection<Film> all = filmStorage.findAll();
        ArrayList<Film> all2 = new ArrayList<>(all);

        return all2.stream()
                .sorted((film1, film2) -> {
                    int film1Int = film1.getLikes();
                    int film2Int = film2.getLikes();
                    return Integer.compare(film2Int, film1Int);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addLike(int filmId, int userId) {
        User user = userService.getUserStorage().getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (user.getFilmLikes().contains(film))
            throw new ValidationException404("Пользователь " + userId + " уже лайкал фильм " + filmId);
        else {
            film.setLikes(film.getLikes() + 1);
            user.getFilmLikes().add(film);
        }
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        User user = userService.getUserStorage().getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (!user.getFilmLikes().contains(film))
            throw new ValidationException404("Пользователь " + userId + " не лайкал фильм " + filmId);
        else {
            film.setLikes(film.getLikes() - 1);
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