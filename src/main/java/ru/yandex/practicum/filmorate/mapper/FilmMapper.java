package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FilmMapper {

    private FilmMapper() {
    }

    public static FilmDTO filmToDTO(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("film cannot be null");
        }

        return FilmDTO.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(film.getLikes())
                .genres(film.getGenres())
                .directors(film.getDirectors())
                .mpa(film.getMpa())
                .build();
    }

    public static Film dtoToFilm(FilmDTO filmDTO) {
        if (filmDTO == null) {
            throw new IllegalArgumentException("filmDTO cannot be null");
        }

        return Film.builder()
                .id(filmDTO.getId())
                .name(filmDTO.getName())
                .description(filmDTO.getDescription())
                .releaseDate(filmDTO.getReleaseDate())
                .duration(filmDTO.getDuration())
                .likes(filmDTO.getLikes())
                .genres(filmDTO.getGenres())
                .directors(filmDTO.getDirectors())
                .mpa(filmDTO.getMpa())
                .build();
    }

    public static List<FilmDTO> listFilmsToListDto(Collection<Film> films) {
        return films.stream().map(FilmMapper::filmToDTO).collect(Collectors.toList());
    }
}
