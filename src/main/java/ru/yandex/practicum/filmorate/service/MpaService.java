package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.MpaDTO;

import java.util.List;

public interface MpaService {

    MpaDTO readById(Integer id);

    List<MpaDTO> readAll();

}
