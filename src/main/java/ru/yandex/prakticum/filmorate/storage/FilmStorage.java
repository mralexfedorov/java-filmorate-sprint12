package ru.yandex.prakticum.filmorate.storage;

import ru.yandex.prakticum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film getById(int id);

    Collection<Film> getAll();

    void add(Film user);

    void update(Film user);
}
