package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
public class UserDTO {

    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String login;

    private String name;
    @Past
    private LocalDate birthday;
}
