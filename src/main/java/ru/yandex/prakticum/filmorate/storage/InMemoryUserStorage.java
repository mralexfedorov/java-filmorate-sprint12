package ru.yandex.prakticum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.prakticum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.prakticum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.prakticum.filmorate.exception.ValidationException;
import ru.yandex.prakticum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User getById(int id) {
        checkUserNotFound(id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void add(User user) {
        int id = user.getId();

        if (id == 0) {
            id = setIdByDefault();
            user.setId(id);
        } else {
            checkUserIdNotNegative(id);
            checkUserAlreadyExist(id);
        }

        checkUserLogin(user.getLogin());
        checkUserBirthday(user.getBirthday());

        setNameByDefault(user);
        user.setFriends(new HashSet<>());
        users.put(id, user);
        log.info("Добавлен пользователь: '{}'", user.toString());
    }

    @Override
    public void update(User user) {
        int id = user.getId();

        checkUserIdNotNull(id);
        checkUserIdNotNegative(id);
        checkUserNotFound(id);
        checkUserLogin(user.getLogin());
        checkUserBirthday(user.getBirthday());

        User updatedUser = getById(id);
        user.setFriends(updatedUser.getFriends());

        users.replace(id, user);
        log.info("Обновлен пользователь: '{}'", user.toString());
    }

    public void checkUserAlreadyExist(int id) {
        if (users.containsKey(id)) {
            log.info("Пользователь с id '{}' уже существует.", id);
            throw new ObjectAlreadyExistException(String.format(
                    "Пользователь с id %s уже существует.",
                    id
            ));
        }
    }

    public void checkUserNotFound(int id) {
        if (!users.containsKey(id)) {
            log.info("Пользователь с id '{}' не найден.", id);
            throw new ObjectNotFoundException(String.format(
                    "Пользователь с id %s не найден.",
                    id
            ));
        }
    }

    public void checkUserIdNotNull(int id) {
        if (id == 0) {
            log.info("id '{}' не заполнен.", id);
            throw new ValidationException("id не заполнен.");
        }
    }

    public void checkUserIdNotNegative(int id) {
        if (id < 0) {
            log.info("id '{}' не может быть отрицательным.", id);
            throw new ValidationException("id не может быть отрицательным.");
        }
    }

    public void checkUserLogin(String login) {
        if (login.contains(" ")) {
            log.info("Логин '{}' не может содержать пробелы.", login);
            throw new ValidationException("Логин не может содержать пробелы.");
        }
    }

    public void checkUserBirthday(LocalDate birthday) {
        if (birthday.isAfter(java.time.LocalDate.now()) ) {
            log.info("Дата рождения '{}' не может быть в будущем.", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    private int setIdByDefault() {
        return users.size() + 1;
    }

    private void setNameByDefault(User user) {
        String name = user.getName();
        if (name.isEmpty()) {
            user.setNameByDefault();
        }
    }
}
