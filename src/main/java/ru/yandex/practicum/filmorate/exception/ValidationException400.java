package ru.yandex.practicum.filmorate.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(value = BAD_REQUEST)
public class ValidationException400 extends RuntimeException {
    public ValidationException400(String message) {
        super(message);
    }
}