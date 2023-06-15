package ru.yandex.practicum.filmorate.model.abstraction;

import lombok.Data;

@Data
public abstract class entityWithID {
    int id;

    public entityWithID(int id) {
        this.id = id;
    }
}
