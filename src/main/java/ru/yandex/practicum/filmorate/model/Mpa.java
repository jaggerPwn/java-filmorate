package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Mpa {

    private Integer id;
    private String name;

    public Mpa(Integer id) {
        this.id = id;
    }

    //o	G — у фильма нет возр
    // астных ограничений,
    //o	PG — детям рекомендуется смотреть фильм с родителями,
    //o	PG-13 — детям до 13 лет просмотр не желателен,
    //o	R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
    //o	NC-17 — лицам до 18 лет просмотр запрещён.
}
