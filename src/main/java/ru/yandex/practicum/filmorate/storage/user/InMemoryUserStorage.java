package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public User add(User user) {
        int id = idGenerator.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        log.debug("Пользователь сохранён в хранилище с id={}", id);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь с id={} обновлён в хранилище", user.getId());
        return user;
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
        log.debug("Пользователь с id={} удалён из хранилища", id);
    }

    @Override
    public List<User> findAll() {
        log.debug("Запрошены все пользователи из хранилища, найдено {} записей", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Integer id) {
        User user = users.get(id);
        if (user == null) {
            log.debug("Пользователь с id={} не найден в хранилище", id);
        }
        return user;
    }
}
