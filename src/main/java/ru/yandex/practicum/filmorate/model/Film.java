package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NotNull(message = "Name should not be null")
    @NotEmpty(message = "Name should not be empty")
    private String name;
    @Size(message = "Too many symbols in description, max=200", max = 200)
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    @Enumerated
    private Genre genre;
    @Enumerated
    private Rating rating;
}
