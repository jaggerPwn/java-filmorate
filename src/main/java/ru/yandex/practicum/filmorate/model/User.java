package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.*;


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
    TreeMap<Integer/*friendId*/, Integer/*status, 1 accepted, 0 unaccepted*/> friends = new TreeMap<>((o1, o2) -> {
        if (o1 > o2) return 1;
        else if (o2 > o1) return -1;
        return 0;
    });
    Set<Film> filmLikes = new HashSet<>();
}
