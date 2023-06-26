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
        log.info("Получен POST запрос по эндпоинту '/films' на создание фильма");
        return new ResponseEntity<>(service.saveFilm(newFilm), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FilmDTO> updateFilm(@RequestBody @Valid FilmDTO newFilm) {
        log.info("Получен PUT запрос по эндпоинту '/films' на обновление фильма");
        return new ResponseEntity<>(service.updateFilm(newFilm), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FilmDTO>> readAllFilms() {
        log.info("Получен GET запрос по эндпоинту '/films' на получение всех фильмов");
        return new ResponseEntity<>(service.readAllFilms(), HttpStatus.OK);
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseEntity<?> userLike(@PathVariable Long id, @PathVariable Long userId) {
        service.userLike(id, userId);
        log.info("Получен PUT запрос по эндпоинту '/films/{id}/like/{userId}' на создание лайка для фильма");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}/like/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLikeById(id, userId);
        log.info("Получен DELETE запрос по эндпоинту '/films/{id}/like/{userId}' на удаление лайка у фильма");
        return new ResponseEntity<>(HttpStatus.valueOf(200));
    }

    @GetMapping("popular")
    public ResponseEntity<List<FilmDTO>> readTopFilms(@RequestParam(required = false, defaultValue = "10") Long count) {
        log.info("Получен GET запрос по эндпоинту '/films/popular' на получение топ фильмов");
        return new ResponseEntity<>(service.readTopFilms(count), HttpStatus.OK);
    }

    @GetMapping("{filmId}")
    public ResponseEntity<FilmDTO> getFilmById(@PathVariable Long filmId) {
        log.info("Получен GET запрос по эндпоинту '/films/{filmId}' на получение фильма по ID");
        return new ResponseEntity<>(service.getFilmByID(filmId), HttpStatus.OK);
    }

}
