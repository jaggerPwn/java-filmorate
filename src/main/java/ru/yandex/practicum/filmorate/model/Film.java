package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    int id;
    @NotNull(message = "Name should not be null")
    @NotEmpty(message = "Name should not be empty")
    String name;
    @Size(message = "Too many symbols in description, max=200", max = 200)
    String description;
    LocalDate releaseDate;
    @Min(0)
    int duration;
}
