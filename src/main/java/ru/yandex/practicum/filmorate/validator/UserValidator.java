package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
@Component
public class UserValidator {

    public void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Ошибка валидации пользователя: {}", "Электронная почта не может быть пустой.");
            throw new ValidationException("Электронная почта не может быть пустой.");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Ошибка валидации пользователя: {}", "Электронная почта должна содержать символ @.");
            throw new ValidationException("Электронная почта должна содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации пользователя: {}", "Логин не может быть пустым.");
            throw new ValidationException("Логин не может быть пустым.");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации пользователя: {}", "Логин не может содержать пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации пользователя: {}", "Дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    public void applyDisplayNameFallback(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
