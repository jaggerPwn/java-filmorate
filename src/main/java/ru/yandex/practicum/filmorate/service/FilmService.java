package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

public interface FilmService {
    FilmStorage getFilmStorage();

    Collection<Film> getPopularFilms(Integer count);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    Collection<Film> deleteAll();

    Mpa getMpa(Integer mpaID);

    Collection<Mpa> getMpa();

    Genres getGenres(Integer genresId);

    Collection<Genres> getGenres();
}
