package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    @Email(regexp = ".+@.+\\..+|")
    private String email;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", message = "Invalid login")
    private String login;
    private String name;
    private LocalDate birthday;
    Set<Integer> friends = new HashSet<>();
}
