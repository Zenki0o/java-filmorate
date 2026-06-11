package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private static final int POPULAR_FILMS_LIMIT = 10;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, FilmValidator filmValidator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmValidator = filmValidator;
    }

    public Film create(Film film) {
        filmValidator.validate(film);
        Film created = filmStorage.add(film);
        log.info("Добавлен фильм с id={}", created.getId());
        return created;
    }

    public Film update(Film film) {
        filmValidator.validate(film);
        Film existingFilm = getById(film.getId());
        film.setLikes(existingFilm.getLikes());
        Film updated = filmStorage.update(film);
        log.info("Обновлён фильм с id={}", updated.getId());
        return updated;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(Integer id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            log.warn("Фильм с id={} не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с указанным идентификатором не найден.");
        }
        return film;
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = getById(filmId);
        if (userStorage.findById(userId) == null) {
            log.warn("Пользователь с id={} не найден", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с указанным идентификатором не найден.");
        }
        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь id={} уже поставил лайк фильму id={}", userId, filmId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь уже поставил лайк этому фильму.");
        }
        film.getLikes().add(userId);
        log.info("Пользователь с id={} поставил лайк фильму с id={}", userId, filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = getById(filmId);
        if (userStorage.findById(userId) == null) {
            log.warn("Пользователь с id={} не найден", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с указанным идентификатором не найден.");
        }
        if (!film.getLikes().contains(userId)) {
            log.warn("Пользователь id={} не ставил лайк фильму id={}", userId, filmId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь не ставил лайк этому фильму.");
        }
        film.getLikes().remove(userId);
        log.info("Пользователь с id={} удалил лайк у фильма с id={}", userId, filmId);
    }

    public List<Film> getPopularFilms() {
        List<Film> popularFilms = filmStorage.findAll().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed()
                        .thenComparing(Film::getId))
                .limit(POPULAR_FILMS_LIMIT)
                .collect(Collectors.toList());
        log.debug("Запрошен список популярных фильмов, возвращено {} фильмов", popularFilms.size());
        return popularFilms;
    }
}
