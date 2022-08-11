package ru.yandex.prakticum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.prakticum.filmorate.exception.ValidationException;
import ru.yandex.prakticum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        int id = user.getId();
        if (id == 0) {
            id = setIdByDefault();
            user.setId(id);
        } else if (id < 0) {
            log.info("id '{}' не может быть отрицательным.", id);
            throw new ValidationException("id не может быть отрицательным.");
        }

        String login = user.getLogin();
        if (login.contains(" ")) {
            log.info("Логин '{}' не может содержать пробелы.", login);
            throw new ValidationException("Логин не может содержать пробелы.");
        }

        LocalDate birthday = user.getBirthday();
        if (birthday.isAfter(java.time.LocalDate.now()) ) {
            log.info("Дата рождения '{}' не может быть в будущем.", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }

        String name = user.getName();
        if (name.isEmpty()) {
            user.setNameByDefault();
        }

        if (users.get(id) == null) {
            users.put(id, user);
        } else {
            log.info("Пользователь '{}' уже существует.", user);
            throw new ValidationException("Пользователь уже существует.");
        }
        log.info("Добавлен пользователь: '{}'", user.toString());

        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        int id = user.getId();
        if (id < 0) {
            log.info("id '{}' не может быть отрицательным.", id);
            throw new ValidationException("id не может быть отрицательным.");
        }

        if (users.get(id) != null) {
            users.replace(id, user);
        } else {
            users.put(id, user);
        }
        log.info("Обновлен пользователь: '{}'", user.toString());

        return user;
    }

    private int setIdByDefault() {
        return users.size() + 1;
    }
}
