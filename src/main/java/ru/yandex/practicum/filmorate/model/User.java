package ru.yandex.practicum.filmorate.model;



import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
