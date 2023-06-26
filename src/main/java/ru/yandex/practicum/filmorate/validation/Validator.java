package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class Validator {

    private Validator() {
    }

    //Для Film:
    //название не может быть пустым;
    //максимальная длина описания — 200 символов;
    //дата релиза — не раньше 28 декабря 1895 года;
    //продолжительность фильма должна быть положительной.
    public static boolean filmValidator(Film film) {
        if (film.getName().isEmpty()) {
            log.debug("Name can not be empty");
            throw new ValidationException("Name can not be empty");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Description size can`t be mre than 200.");
            throw new ValidationException("Description size can`t be mre than 200.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Date is wrong");
            throw new ValidationException("Date is wrong");
        }
        if (film.getDuration() <= 0) {
            log.debug("Duration must be positive");
            throw new ValidationException("Duration must be positive");
        }
        log.debug("Validation is successful");
        return true;
    }

    //Для User:
    //электронная почта не может быть пустой и должна содержать символ @;
    //логин не может быть пустым и содержать пробелы;
    //имя для отображения может быть пустым — в таком случае будет использован логин;
    //дата рождения не может быть в будущем.
    public static boolean userValidator(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.debug("Email is wrong");
            throw new ValidationException("Email is wrong");
        }
        if (user.getLogin().isBlank()) {
            log.debug("Login is wrong");
            throw new ValidationException("Login is wrong");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Username is Blanc, login is username");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Birthday can`t be in future");
            throw new ValidationException("Birthday can`t be in future");
        }
        log.debug("User validation is successful");
        return true;
    }

}
