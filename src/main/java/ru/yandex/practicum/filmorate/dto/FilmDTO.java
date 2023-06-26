package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class FilmDTO {

    private Long id;

    @NotBlank
    private String name;

    @Size(min = 0, max = 199)
    private String description;

    @Past
    private LocalDate releaseDate;

    @PositiveOrZero
    private Integer duration;
    private Set<Long> likes;
    private Set<Genre> genres;
    private Mpa mpa;
}
