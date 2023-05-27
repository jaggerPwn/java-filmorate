package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;

@Data
@Builder
public class Film {
    private int id;
    @NotNull(message = "Name should not be null")
    @NotEmpty(message = "Name should not be empty")
    private final String name;
    @Size(message = "Too many symbols in description, max=200", max = 200)
    private String description;
    private final LocalDate releaseDate;
    @Min(1)
    private int duration;
    @Nullable
    private Collection<Genres> genres;
    @Nullable
    private Mpa mpa;
}
