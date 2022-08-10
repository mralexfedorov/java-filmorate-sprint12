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
    private final HashMap<String, User> users = new HashMap<>();

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
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

        String email = user.getEmail();
        if (users.get(email) == null) {
            users.put(email, user);
        } else {
            log.info("Пользователь '{}' уже существует.", user);
            throw new ValidationException("Пользователь уже существует.");
        }
        log.info("Добавлен пользователь: '{}'", user.toString());

        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        String email = user.getEmail();

        if (users.get(email) != null) {
            users.replace(email, user);
        } else {
            log.info("Пользователь '{}' не найден.", user);
            throw new ValidationException("Пользователь не найден.");
        }
        log.info("Обновлен пользователь: '{}'", user.toString());

        return user;
    }
}
