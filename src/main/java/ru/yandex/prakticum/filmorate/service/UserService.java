package ru.yandex.prakticum.filmorate.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.prakticum.filmorate.model.User;
import ru.yandex.prakticum.filmorate.storage.FriendsDBStorage;
import ru.yandex.prakticum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    @NonNull
    @Qualifier("userDbStorage")
    private UserStorage userStorage;
    @NonNull
    private FriendsDBStorage friendsDBStorage;

    public void addFriend(int id, int friendId) {
        userStorage.checkUserNotFound(id);
        userStorage.checkUserNotFound(friendId);

        friendsDBStorage.add(id, friendId);

        log.info("У пользователя с id: '{}' появился друг c id: '{}'", id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        userStorage.checkUserNotFound(id);
        userStorage.checkUserNotFound(friendId);

        friendsDBStorage.delete(id, friendId);
        friendsDBStorage.delete(friendId, id);

        log.info("Пользователь с id: '{}' отписался от пользователя с id: '{}'", friendId, id);
    }

    public Collection<User> getUserFriends(int id) {
        userStorage.checkUserNotFound(id);

        List<Integer> userFriendsIds = friendsDBStorage.getUserFriendsIds(id);
        List<User> userFriends = new ArrayList<>();

        for (Integer userId: userFriendsIds) {
            userFriends.add(userStorage.getById(userId));
        }
        return userFriends;
    }

    public Collection<User> getCommonFriends(int id, int otherId) {
        userStorage.checkUserNotFound(id);
        userStorage.checkUserNotFound(otherId);

        List<Integer> userFriendsIds = friendsDBStorage.getUserFriendsIds(id);
        List<Integer> otherUserFriendsIds = friendsDBStorage.getUserFriendsIds(otherId);

        List<User> commonUsers = new ArrayList<>();

        for (int friendId: userFriendsIds) {
            if (otherUserFriendsIds.contains(friendId)) {
                commonUsers.add(userStorage.getById(friendId));
            }
        }

        return commonUsers;
    }
}
