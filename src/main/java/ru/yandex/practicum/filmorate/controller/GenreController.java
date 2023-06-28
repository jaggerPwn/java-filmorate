package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> readAll() {
        log.info("Получен GET запрос по эндпоинту '/genres' на получение всех Genres");
        return new ResponseEntity<>(genreService.readAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> readById(@PathVariable Integer id) {
        log.info("Получен GET запрос по эндпоинту '/genres/{}' на получение Genre по id", id);
        return new ResponseEntity<>(genreService.readById(id), HttpStatus.OK);
    }

}
