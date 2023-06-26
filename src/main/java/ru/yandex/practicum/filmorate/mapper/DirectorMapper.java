package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DirectorMapper {
    private DirectorMapper() {
    }

    public static Director dtoToDirector(DirectorDTO directorDTO) {
        if (directorDTO == null) {
            return null;
        }

        return Director.builder()
                .id(directorDTO.getId())
                .name(directorDTO.getName())
                .build();
    }

    public static DirectorDTO directorToDTO(Director director) {
        if (director == null) {
            return null;
        }

        return DirectorDTO.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }

    public static List<DirectorDTO> listDirectorToListDto(Collection<Director> directors) {
        return directors.stream().map(DirectorMapper::directorToDTO).collect(Collectors.toList());
    }
}
