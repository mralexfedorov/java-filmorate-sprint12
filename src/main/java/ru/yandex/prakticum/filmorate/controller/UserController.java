package ru.yandex.prakticum.filmorate.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.prakticum.filmorate.model.User;
import ru.yandex.prakticum.filmorate.service.UserService;
import ru.yandex.prakticum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Component
public class UserController {
    @NonNull
    private InMemoryUserStorage inMemoryUserStorage;
    @NonNull
    private UserService userService;

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable int id) {
        return inMemoryUserStorage.getById(id);
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        inMemoryUserStorage.add(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        inMemoryUserStorage.update(user);
        return user;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
