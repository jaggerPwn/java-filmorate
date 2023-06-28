package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.DirectorDTO;

import java.util.List;

public interface DirectorService {
    DirectorDTO saveDirector(DirectorDTO directorDTO);

    DirectorDTO updateDirector(DirectorDTO directorDTO);

    List<DirectorDTO> readAllDirectors();

    DirectorDTO getDirectorById(Long id);

    void deleteDirectorById(Long id);
}
