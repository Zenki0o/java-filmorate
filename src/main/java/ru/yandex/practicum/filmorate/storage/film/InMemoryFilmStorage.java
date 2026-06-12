package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Film add(Film film) {
        int id = idGenerator.getAndIncrement();
        film.setId(id);
        films.put(id, film);
        log.debug("Фильм сохранён в хранилище с id={}", id);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.debug("Фильм с id={} обновлён в хранилище", film.getId());
        return film;
    }

    @Override
    public void delete(Integer id) {
        films.remove(id);
        log.debug("Фильм с id={} удалён из хранилища", id);
    }

    @Override
    public List<Film> findAll() {
        log.debug("Запрошены все фильмы из хранилища, найдено {} записей", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Integer id) {
        Film film = films.get(id);
        if (film == null) {
            log.debug("Фильм с id={} не найден в хранилище", id);
        }
        return film;
    }
}
