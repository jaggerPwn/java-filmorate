package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Genre {

    private Integer id;
    private String name;

    //o	Комедия.
    //o	Драма.
    //o	Мультфильм.
    //o	Триллер.
    //o	Документальный.
    //o	Боевик.
}
