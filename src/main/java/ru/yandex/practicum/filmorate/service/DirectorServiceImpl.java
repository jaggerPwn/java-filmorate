package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public DirectorDTO saveDirector(DirectorDTO directorDTO) {
        Director director = DirectorMapper.dtoToDirector(directorDTO);
        DirectorDTO dirDTO = DirectorMapper.directorToDTO(directorStorage.saveDirector(director));
        log.debug("Director c ID {} сохранён.", directorDTO.getId());
        return dirDTO;
    }

    @Override
    public DirectorDTO updateDirector(DirectorDTO directorDTO) {
        Director director = DirectorMapper.dtoToDirector(directorDTO);
        DirectorDTO dirDTO = DirectorMapper.directorToDTO(directorStorage.updateDirector(director));
        log.debug("Director c ID {} обновлен.", directorDTO.getId());
        return dirDTO;
    }

    @Override
    public List<DirectorDTO> readAllDirectors() {
        log.debug("Получен список всех Directors");
        return DirectorMapper.listDirectorToListDto(directorStorage.readAllDirectors());
    }

    @Override
    public DirectorDTO getDirectorById(Long id) {
        log.debug("Director c ID c {} получен.", id);
        return DirectorMapper.directorToDTO(directorStorage.getDirectorById(id));
    }

    @Override
    public void deleteDirectorById(Long id) {
        directorStorage.deleteDirectorById(id);
        log.debug("Director с ID {} удален", id);
    }
}
