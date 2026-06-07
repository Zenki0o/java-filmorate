package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final UserValidator userValidator;

    public UserController(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        userValidator.validate(user);
        userValidator.applyDisplayNameFallback(user);
        int id = idGenerator.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        log.info("Создан пользователь с id={}", id);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        userValidator.validate(user);
        userValidator.applyDisplayNameFallback(user);
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Ошибка обновления пользователя: пользователь с id={} не найден", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с указанным идентификатором не найден.");
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь с id={}", user.getId());
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
