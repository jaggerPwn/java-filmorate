package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getFilmStorage().findAll();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable("id") int id) {
        return filmService.getFilmStorage().getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        return filmService.getFilmStorage().create(film);
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        return filmService.getFilmStorage().put(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        return filmService.deleteLike(filmId, userId);
    }
}
//
//        PUT    /films/{id}/like/{userId} — пользователь ставит лайк фильму.
//        DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
//        GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.