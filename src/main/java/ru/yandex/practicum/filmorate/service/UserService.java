package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final UserValidator userValidator;
    private final Map<Integer, Set<Integer>> friendships = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage, UserValidator userValidator) {
        this.userStorage = userStorage;
        this.userValidator = userValidator;
    }

    public User create(User user) {
        userValidator.validate(user);
        userValidator.applyDisplayNameFallback(user);
        User created = userStorage.add(user);
        log.info("Создан пользователь с id={}", created.getId());
        return created;
    }

    public User update(User user) {
        userValidator.validate(user);
        userValidator.applyDisplayNameFallback(user);
        getById(user.getId());
        User updated = userStorage.update(user);
        log.info("Обновлён пользователь с id={}", updated.getId());
        return updated;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(Integer id) {
        User user = userStorage.findById(id);
        if (user == null) {
            log.warn("Пользователь с id={} не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с указанным идентификатором не найден.");
        }
        return user;
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            log.warn("Попытка добавить пользователя id={} в друзья самому себе", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя добавить самого себя в друзья.");
        }
        getById(userId);
        getById(friendId);
        friendships.computeIfAbsent(userId, id -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, id -> new HashSet<>()).add(userId);
        log.info("Пользователь с id={} добавил в друзья пользователя с id={}", userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        getById(userId);
        getById(friendId);
        Set<Integer> userFriends = friendships.get(userId);
        if (userFriends == null || !userFriends.contains(friendId)) {
            log.warn("Пользователь id={} не является другом пользователя id={}", friendId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь не является другом.");
        }
        userFriends.remove(friendId);
        friendships.get(friendId).remove(userId);
        log.info("Пользователь с id={} удалил из друзей пользователя с id={}", userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        getById(userId);
        Set<Integer> friendIds = friendships.getOrDefault(userId, Set.of());
        log.debug("Запрошен список друзей пользователя id={}, найдено {} друзей", userId, friendIds.size());
        return friendIds.stream()
                .map(userStorage::findById)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        getById(userId);
        getById(otherId);
        Set<Integer> userFriends = friendships.getOrDefault(userId, Set.of());
        Set<Integer> otherFriends = friendships.getOrDefault(otherId, Set.of());
        List<User> commonFriends = userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::findById)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("Запрошены общие друзья пользователей id={} и id={}, найдено {} общих друзей",
                userId, otherId, commonFriends.size());
        return commonFriends;
    }
}
