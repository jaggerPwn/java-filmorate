package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface Storage<T> {
    Collection<T> findAll();

    T create(T user);

    T update(T user);

    T getById(int id);

    void clear();
}
