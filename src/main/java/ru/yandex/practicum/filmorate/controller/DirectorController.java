package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public ResponseEntity<List<DirectorDTO>> readAllDirectors() {
        log.info("Получен GET запрос по эндпоинту '/directors' на получение всех directors");
        return new ResponseEntity<>(directorService.readAllDirectors(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DirectorDTO> createDirector(@RequestBody @Valid DirectorDTO newDirector) {
        log.info("Получен POST запрос по эндпоинту '/directors' на создание director {}.", newDirector.getName());
        return new ResponseEntity<>(directorService.saveDirector(newDirector), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<DirectorDTO> updateDirector(@RequestBody @Valid DirectorDTO director) {
        log.info("Получен PUT запрос по эндпоинту '/directors' на обновление director c ID {}.", director.getId());
        return new ResponseEntity<>(directorService.updateDirector(director), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<DirectorDTO> getDirectorById(@PathVariable Long id) {
        log.info("Получен GET запрос по эндпоинту '/directors/{}' на получение director по ID {}.", id, id);
        return new ResponseEntity<>(directorService.getDirectorById(id), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<DirectorDTO> deleteDirectorById(@PathVariable Long id) {
        log.info("Получен DELETE запрос по эндпоинту '/directors/{}' на удаление directorпо ID {}.", id, id);
        directorService.deleteDirectorById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
