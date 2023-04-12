package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    int id;
    @Email(regexp = ".+@.+\\..+|")
    private String email;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", message = "Invalid login")
    String login;
    String name;
    LocalDate birthday;
}
