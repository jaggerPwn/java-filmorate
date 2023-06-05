package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping
public class OtherController {
    private final FilmService filmService;

    public OtherController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable("id") Integer mpaID) {
        return filmService.getMpa(mpaID);
    }

    @GetMapping("/mpa")
    public Collection<Mpa> getMpa() {
        return filmService.getMpa();
    }

    @GetMapping("/genres/{id}")
    public Genres getGenre(@PathVariable("id") Integer mpaID) {
        return filmService.getGenres(mpaID);
    }

    @GetMapping("/genres")
    public Collection<Genres> getGenre() {
        return filmService.getGenres();
    }

}
