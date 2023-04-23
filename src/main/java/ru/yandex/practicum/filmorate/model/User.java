package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Data

public class User {
    private int id;
    @Email(regexp = ".+@.+\\..+|")
    private String email;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", message = "Invalid login")
    private String login;
    private String name;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    Set<Integer> friends = new TreeSet<>((o1, o2) -> {
        if(o1>o2) return 1;
        else if (o2>o1) return -1;
        return 0;
    });
}
