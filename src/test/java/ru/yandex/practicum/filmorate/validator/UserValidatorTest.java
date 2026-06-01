package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorTest {

    private final UserValidator userValidator = new UserValidator();

    @Test
    void validateValidUser() {
        assertDoesNotThrow(() -> userValidator.validate(validUser()));
    }

    @Test
    void validateRejectsNullEmail() {
        User user = validUser();
        user.setEmail(null);

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateRejectsEmptyEmail() {
        User user = validUser();
        user.setEmail("");

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateRejectsBlankEmail() {
        User user = validUser();
        user.setEmail("   ");

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateRejectsEmailWithoutAt() {
        User user = validUser();
        user.setEmail("invalid-email");

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateRejectsNullLogin() {
        User user = validUser();
        user.setLogin(null);

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateRejectsEmptyLogin() {
        User user = validUser();
        user.setLogin("");

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateRejectsBlankLogin() {
        User user = validUser();
        user.setLogin("   ");

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateRejectsLoginWithSpaces() {
        User user = validUser();
        user.setLogin("log in");

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateAcceptsNullName() {
        User user = validUser();
        user.setName(null);

        assertDoesNotThrow(() -> userValidator.validate(user));
    }

    @Test
    void validateAcceptsBlankName() {
        User user = validUser();
        user.setName("   ");

        assertDoesNotThrow(() -> userValidator.validate(user));
    }

    @Test
    void validateAcceptsNullBirthday() {
        User user = validUser();
        user.setBirthday(null);

        assertDoesNotThrow(() -> userValidator.validate(user));
    }

    @Test
    void validateAcceptsBirthdayToday() {
        User user = validUser();
        user.setBirthday(LocalDate.now());

        assertDoesNotThrow(() -> userValidator.validate(user));
    }

    @Test
    void validateRejectsBirthdayInFuture() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userValidator.validate(user));
    }

    @Test
    void validateRejectsEmptyUser() {
        assertThrows(ValidationException.class, () -> userValidator.validate(new User()));
    }

    @Test
    void applyDisplayNameFallbackUsesLoginWhenNameIsNull() {
        User user = validUser();
        user.setName(null);

        userValidator.applyDisplayNameFallback(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void applyDisplayNameFallbackUsesLoginWhenNameIsBlank() {
        User user = validUser();
        user.setName("   ");

        userValidator.applyDisplayNameFallback(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void applyDisplayNameFallbackKeepsNameWhenPresent() {
        User user = validUser();
        user.setName("Display Name");

        userValidator.applyDisplayNameFallback(user);

        assertEquals("Display Name", user.getName());
    }

    private static User validUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}
