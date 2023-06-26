package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>(); // для хранения фильмов
    private Long genId = 1L;

    @Override
    public Film saveFilm(Film film) {
        film.setId(genId);
        films.put(film.getId(), film);
        genId++;
        log.debug("Film c ID {} успешно сохранён.", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Film c ID {} успешно обновлён", film.getId());
            return film;
        } else {
            log.debug("Film c ID {} не обновлён", film.getId());
            throw new EntityNotFoundException("Нет такого ID");
        }
    }

    @Override
    public List<Film> readAllFilms() {
        log.debug("Получен список всех Film");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {

        if (films.containsKey(id)) {
            log.debug("Film с ID {} получен", id);
            return films.get(id);
        } else {
            log.debug("Film с ID {} не получен", id);
            throw new EntityNotFoundException("Film с ID не найден");
        }
    }

    public List<Film> getSortedFilms(Long id, String sortBy) {
        return null;
    }

    @Override
    public List<Film> searchFilmForDirector(String queryStr) {
        return null;
    }

    @Override
    public List<Film> searchFilmForTitle(String queryStr) {
        return null;
    }

    @Override
    public List<Film> searchFilmForTitleAndDirector(String queryStr) {
        return null;
    }

    @Override
    public List<Film> getTopFilms(Long count, Long genreId, Long year) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return null;
    }


    @Override
    public void deleteFilm(Long id) {
    }
}
