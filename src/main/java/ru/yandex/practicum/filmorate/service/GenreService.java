package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.GenreDTO;

import java.util.List;

public interface GenreService {

    GenreDTO readById(Integer id);

    List<GenreDTO> readAll();

}
