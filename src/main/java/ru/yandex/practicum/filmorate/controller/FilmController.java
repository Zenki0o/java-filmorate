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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final FilmValidator filmValidator;

    public FilmController(FilmValidator filmValidator) {
        this.filmValidator = filmValidator;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        filmValidator.validate(film);
        int id = idGenerator.getAndIncrement();
        film.setId(id);
        films.put(id, film);
        log.info("Добавлен фильм с id={}", id);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        filmValidator.validate(film);
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Ошибка обновления фильма: фильм с id={} не найден", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с указанным идентификатором не найден.");
        }
        films.put(film.getId(), film);
        log.info("Обновлён фильм с id={}", film.getId());
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}
