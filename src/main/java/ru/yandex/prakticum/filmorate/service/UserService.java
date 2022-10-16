package ru.yandex.prakticum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.prakticum.filmorate.model.User;
import ru.yandex.prakticum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addFriend(int id, int friendId) {
        inMemoryUserStorage.checkUserNotFound(id);
        inMemoryUserStorage.checkUserNotFound(friendId);

        User user  = inMemoryUserStorage.getById(id);
        User friend  = inMemoryUserStorage.getById(friendId);
        user.addFriend(friendId);
        friend.addFriend(id);
        inMemoryUserStorage.update(user);
        inMemoryUserStorage.update(friend);

        log.info("У пользователя с id: '{}' появился друг c id: '{}'", id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        inMemoryUserStorage.checkUserNotFound(id);
        inMemoryUserStorage.checkUserNotFound(friendId);

        User user  = inMemoryUserStorage.getById(id);
        User friend  = inMemoryUserStorage.getById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(id);
        inMemoryUserStorage.update(user);
        inMemoryUserStorage.update(friend);

        log.info("Пользователь с id: '{}' отписался от пользователя с id: '{}'", friendId, id);
    }

    public Collection<User> getUserFriends(int id) {
        inMemoryUserStorage.checkUserNotFound(id);

        Set<Integer> userFriendsIds = inMemoryUserStorage.getById(id).getFriends();
        List<User> userFriends = new ArrayList<>();

        for (Integer userId: userFriendsIds) {
            userFriends.add(inMemoryUserStorage.getById(userId));
        }
        return userFriends;
    }

    public Collection<User> getCommonFriends(int id, int otherId) {
        inMemoryUserStorage.checkUserNotFound(id);
        inMemoryUserStorage.checkUserNotFound(otherId);

        Set<Integer> userFriends = inMemoryUserStorage.getById(id).getFriends();
        Set<Integer> otherUserFriends = inMemoryUserStorage.getById(otherId).getFriends();
        List<User> commonUsers = new ArrayList<>();

        for (int friendId: userFriends) {
            if (otherUserFriends.contains(friendId)) {
                commonUsers.add(inMemoryUserStorage.getById(friendId));
            }
        }

        return commonUsers;
    }
}
