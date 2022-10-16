package ru.yandex.prakticum.filmorate.storage;

import ru.yandex.prakticum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User getById(int id);

    Collection<User> getAll();

    void add(User user);

    void update(User user);
}
