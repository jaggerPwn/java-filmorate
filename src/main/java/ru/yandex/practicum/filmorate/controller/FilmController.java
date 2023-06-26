package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FilmDTO> createFilm(@RequestBody @Valid FilmDTO newFilm) {
        log.info("Получен POST запрос по эндпоинту '/films' на создание Film");
        return new ResponseEntity<>(service.saveFilm(newFilm), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FilmDTO> updateFilm(@RequestBody @Valid FilmDTO newFilm) {
        log.info("Получен PUT запрос по эндпоинту '/films' на обновление Film");
        return new ResponseEntity<>(service.updateFilm(newFilm), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FilmDTO>> readAllFilms() {
        log.info("Получен GET запрос по эндпоинту '/films' на получение всех Films");
        return new ResponseEntity<>(service.readAllFilms(), HttpStatus.OK);
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseEntity<?> userLike(@PathVariable Long id, @PathVariable Long userId) {
        service.userLike(id, userId);
        log.info(
                "Получен PUT запрос по эндпоинту '/films/{}/like/{}' на создание Like для Film c ID {} от User с ID {}",
                id, userId, id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}/like/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLikeById(id, userId);
        log.info(
                "Получен DELETE запрос по эндпоинту '/films/{}/like/{}' на удаление Like у Film c ID {} от User с ID {}",
                id, userId, id, userId);
        return new ResponseEntity<>(HttpStatus.valueOf(200));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDTO>> getTopFilms(
            @RequestParam(required = false, defaultValue = "10") Long count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long year) {
        log.info("Получен GET запрос по эндпоинту '/films/popular' на получение топ {} Film c GenreId {} за год {}",
                count, genreId, year);
        return new ResponseEntity<>(service.getTopFilms(count, genreId, year), HttpStatus.OK);
    }

    @GetMapping("{filmId}")
    public ResponseEntity<FilmDTO> getFilmById(@PathVariable Long filmId) {
        log.info("Получен GET запрос по эндпоинту '/films/{}' на получение Film по ID", filmId);
        return new ResponseEntity<>(service.getFilmByID(filmId), HttpStatus.OK);
    }

    @GetMapping("/common")
    public ResponseEntity<List<FilmDTO>> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Получен GET запрос по эндпоинту '/films/common' на получение общих Film у двух Users");
        return new ResponseEntity<>(service.getCommonFilms(userId, friendId), HttpStatus.OK);
    }

    @DeleteMapping("/{filmId}")
    public ResponseEntity<?> deleteFilm(@PathVariable Long filmId) {
        log.info("Получен DELETE запрос по эндпоинту '/films/{}' на удаление Film", filmId);
        service.deleteFilm(filmId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/director/{directorId}")
    public ResponseEntity<List<FilmDTO>> getFilmsByDirectorId(@PathVariable Long directorId,
            @RequestParam String sortBy) {
        log.info("Получен GET запрос по эндпоинту '/films/director/{}' на получение по ID director " +
                "отсортированного списка Film по кол-ву likes или releaseDate", directorId);
        return new ResponseEntity<>(service.getSortedFilms(directorId, sortBy), HttpStatus.OK);
    }

    @GetMapping(value = "search")
    public ResponseEntity<List<FilmDTO>> searchFilm(@RequestParam(required = false) String query,
            @RequestParam(required = false) String by) {
        log.info("Получен GET запрос по эндпоинту '/films/search/' на получение по списка Film по " +
                "называнию {} и/или director {} .", query, by);
        return new ResponseEntity<>(service.searchFilm(query, by), HttpStatus.OK);
    }
}
