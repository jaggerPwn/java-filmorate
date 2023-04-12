package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
