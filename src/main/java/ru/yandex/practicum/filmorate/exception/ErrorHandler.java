package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException exception) {
        log.warn("Ошибка валидации: {}", exception.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleNegativeCount(IllegalArgumentException exception) {
        log.warn("Некорректный аргумент запроса: {}", exception.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Передан отрицательный параметр count.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("Некорректное тело запроса: {}", exception.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Тело запроса отсутствует или имеет неверный формат.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
        String message = exception.getReason() != null ? exception.getReason() : exception.getMessage();
        log.warn("Ошибка обработки запроса ({}): {}", exception.getStatusCode().value(), message);
        return buildErrorResponse(exception.getStatusCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("Внутренняя ошибка сервера", exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера.");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatusCode status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message));
    }
}
