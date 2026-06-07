package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmValidatorTest {

    private final FilmValidator filmValidator = new FilmValidator();

    @Test
    void validateValidFilm() {
        assertDoesNotThrow(() -> filmValidator.validate(validFilm()));
    }

    @Test
    void validateRejectsNullName() {
        Film film = validFilm();
        film.setName(null);

        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateRejectsEmptyName() {
        Film film = validFilm();
        film.setName("");

        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateRejectsBlankName() {
        Film film = validFilm();
        film.setName("   ");

        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateAcceptsDescriptionExactly200Characters() {
        Film film = validFilm();
        film.setDescription("a".repeat(200));

        assertDoesNotThrow(() -> filmValidator.validate(film));
    }

    @Test
    void validateRejectsDescriptionLongerThan200Characters() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));

        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateAcceptsNullDescription() {
        Film film = validFilm();
        film.setDescription(null);

        assertDoesNotThrow(() -> filmValidator.validate(film));
    }

    @Test
    void validateAcceptsReleaseDateOnCinemaBirthday() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        assertDoesNotThrow(() -> filmValidator.validate(film));
    }

    @Test
    void validateRejectsReleaseDateBeforeCinemaBirthday() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateAcceptsNullReleaseDate() {
        Film film = validFilm();
        film.setReleaseDate(null);

        assertDoesNotThrow(() -> filmValidator.validate(film));
    }

    @Test
    void validateAcceptsMinimalPositiveDuration() {
        Film film = validFilm();
        film.setDuration(1);

        assertDoesNotThrow(() -> filmValidator.validate(film));
    }

    @Test
    void validateRejectsNullDuration() {
        Film film = validFilm();
        film.setDuration(null);

        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateRejectsZeroDuration() {
        Film film = validFilm();
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateRejectsNegativeDuration() {
        Film film = validFilm();
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> filmValidator.validate(film));
    }

    @Test
    void validateRejectsEmptyFilm() {
        assertThrows(ValidationException.class, () -> filmValidator.validate(new Film()));
    }

    private static Film validFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        return film;
    }
}
